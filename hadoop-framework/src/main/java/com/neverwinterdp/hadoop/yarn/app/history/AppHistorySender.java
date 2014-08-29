package com.neverwinterdp.hadoop.yarn.app.history;

import io.netty.handler.codec.http.HttpResponse;

import java.net.ConnectException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.netty.http.client.AsyncHttpClient;
import com.neverwinterdp.netty.http.client.ResponseHandler;
import com.neverwinterdp.util.UrlParser;

public class AppHistorySender {
  static private Logger logger = LoggerFactory.getLogger(AppHistorySender.class);
  
  static public enum Error { None, NotAvailable, Error };
  
  private String    addressUrl ;
  private UrlParser urlParser  ;
  private long      lastConnectTime ;
  private Error     error = Error.None;
  
  private Thread senderThread ;
  
  private AsyncHttpClient client ;
  
  public AppHistorySender(AppMaster appMaster) {
    this.addressUrl = appMaster.getAppInfo().appHistoryServerAddress ;
    connect() ;
  }
  
  public AppHistorySender(String addressUrl) {
    this.addressUrl = addressUrl ;
    connect() ;
  }

  void connect() {
    try {
      if(addressUrl == null) {
        error = Error.NotAvailable ;
        return ;
      }
      lastConnectTime = System.currentTimeMillis() ;
      urlParser = new UrlParser(addressUrl) ;
      ResponseHandler handler = new ResponseHandler() {
        public void onResponse(HttpResponse response) {
          
        }
      };
      client = new AsyncHttpClient (urlParser.getHost(), urlParser.getPort(), handler) ;
      error = Error.None ;
    } catch(ConnectException ex) {
      error = Error.Error ;
      logger.error("Connect Exception: ", ex);
    } catch(Exception ex) {
      error = Error.Error ;
      logger.error("Unknown Exception: ", ex);
    }
  }
  
  public void send(AppHistory history) {
    if(error == Error.NotAvailable) {
      return ;
    } else if(error == Error.Error) {
      if(System.currentTimeMillis() - 60000 > lastConnectTime) {
        connect() ;
        send(history) ;
        return ;
      } else {
        return ;
      }
    }
    try {
      client.post(urlParser.getPath(), history);
    } catch (Exception e) {
      logger.error("Send Error", e);
      client.close(); 
      error = Error.Error ;
    }
  }
  
  public void startAutoSend(AppMaster appMaster) {
    if(senderThread != null) return ;
    senderThread = new SenderThread(appMaster) ;
    senderThread.start(); 
  }
  
  public void shutdown() { 
    if(senderThread != null) {
      senderThread.interrupt();
      senderThread = null ;
    }
    if(client != null) client.close(); 
  }
  
  
  
  public class SenderThread extends Thread {
    AppMaster appMaster ;
    
    public SenderThread(AppMaster appMaster) {
      this.appMaster = appMaster ;
    }
    public void run() {
      try {
        while(true) {
          send(new AppHistory(appMaster)) ;
          Thread.sleep(3000);
        }
      } catch(InterruptedException ex) {
        send(new AppHistory(appMaster)) ;
      }
    }
  }
}
