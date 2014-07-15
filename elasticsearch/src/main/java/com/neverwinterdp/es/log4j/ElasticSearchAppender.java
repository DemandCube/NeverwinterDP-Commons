package com.neverwinterdp.es.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import com.neverwinterdp.es.ESClient;
import com.neverwinterdp.es.ESObjectClient;
import com.neverwinterdp.util.text.StringUtil;

public class ElasticSearchAppender extends AppenderSkeleton {
  private String[] connect ;
  private String   indexName ;
  private int      maxRetry = 3;
  private long     retryPeriod = 1000 ;
  private long     reconnectPeriod = 1000 * 3 ;
  
  private long     reconnectTime  = 0 ;
  
  private ESObjectClient<Log4jRecord> esLog4jRecordClient ;
  
  public void close() {
  }

  public void setIndexName(String indexName) {
    this.indexName = indexName ;
  }
  
  public void setMaxRetry(int maxRetry) {
    this.maxRetry = maxRetry ;
  }
  
  
  public void setRetryPeriod(long period) {
    retryPeriod = period ;
  }

  public void setReconnectPeriod(long reconnectPeriod) {
    this.reconnectPeriod = reconnectPeriod ;
  }
  
  public void setConnects(String connects) {
    this.connect = StringUtil.toStringArray(connects) ;
  }
  
  public boolean requiresLayout() { return false; }

  protected void append(LoggingEvent event) {
    Log4jRecord record = new Log4jRecord(event) ;
    try {
      ESObjectClient<Log4jRecord> client = getESObjectClient() ; 
      if(client == null) {
        System.err.println("Broken elasticsearch connection");
        return ;
      }
      for(int i = 0 ; i < this.maxRetry; i++) {
        if(doAppend(client, record)) return  ;
        Thread.sleep(retryPeriod) ;
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  private boolean doAppend(ESObjectClient<Log4jRecord> client, Log4jRecord record) {
    try {
      client.put(record, record.getId());
      return true ;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false ;
  }
  
  ESObjectClient<Log4jRecord> getESObjectClient() {
    if(esLog4jRecordClient != null) return esLog4jRecordClient ;
    synchronized(this) {
      if(esLog4jRecordClient != null) return esLog4jRecordClient ;
      if(reconnectTime > System.currentTimeMillis()) return null ;
      reconnectTime = System.currentTimeMillis() + this.reconnectPeriod; 
      try {
        ESClient esclient = new ESClient(connect);
        esLog4jRecordClient = new ESObjectClient<Log4jRecord>(esclient, indexName, Log4jRecord.class) ;
        if (!esLog4jRecordClient.isCreated()) {
          esLog4jRecordClient.createIndexWith(null, null);
        }
      } catch (Throwable error) {
        error.printStackTrace() ;
        releaseESObjectClient() ;
        return null ;
      }
    }
    return esLog4jRecordClient ;
  }
  
  void releaseESObjectClient() {
    if(esLog4jRecordClient != null) {
      esLog4jRecordClient.close(); 
      esLog4jRecordClient = null ;
    }
  }
}