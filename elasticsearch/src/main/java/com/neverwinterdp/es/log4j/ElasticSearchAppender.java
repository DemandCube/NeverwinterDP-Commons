package com.neverwinterdp.es.log4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import com.neverwinterdp.es.ESClient;
import com.neverwinterdp.es.ESObjectClient;
import com.neverwinterdp.util.text.StringUtil;

public class ElasticSearchAppender extends AppenderSkeleton implements Runnable {
  private String[] connect ;
  private String   indexName ;
  private int      maxRetry = 3;
  private long     retryPeriod = 1000 ;
  private long     reconnectPeriod = 1000 ;
  
  private long     reconnectTime  = 0 ;
  
  private LinkedBlockingQueue<Log4jRecord> queue = new LinkedBlockingQueue<Log4jRecord>(30000) ; 
  
  private ESObjectClient<Log4jRecord> esLog4jRecordClient ;
  private Thread forwardThread ;
  
  public void close() {
  }

  public void activateOptions() {
    System.out.println("ACTIVATE: Elasticsearch log4j appender");
    forwardThread = new Thread(this); 
    forwardThread.start() ;
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
      queue.offer(record, 1000, TimeUnit.MILLISECONDS) ;
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }
  }
  
  ESObjectClient<Log4jRecord> getESObjectClient() throws Exception {
    ESClient esclient = new ESClient(connect);
    esLog4jRecordClient = new ESObjectClient<Log4jRecord>(esclient, indexName, Log4jRecord.class) ;
    if (!esLog4jRecordClient.isCreated()) {
      esLog4jRecordClient.createIndexWith(null, null);
    }
    return esLog4jRecordClient ;
  }
  
  void releaseESObjectClient() {
    if(esLog4jRecordClient != null) {
      esLog4jRecordClient.close(); 
      esLog4jRecordClient = null ;
    }
  }
  

  public void run() {
    ESObjectClient<Log4jRecord> client = null ; 
    while(client == null) {
      try {
        Thread.sleep(reconnectPeriod);
        client = getESObjectClient() ;
      } catch(Throwable t) {
        t.printStackTrace();
      }
    }
    
    while(true) {
      try {
        Log4jRecord record = queue.take() ;
        for(int i = 0 ; i < this.maxRetry; i++) {
          if(!doAppend(client, record)) {
            Thread.sleep(retryPeriod) ;
          }
        }
      } catch (InterruptedException e) {
        e.printStackTrace() ;
        return ;
      } catch(Throwable t) {
        t.printStackTrace() ; 
        return ;
      }
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
}