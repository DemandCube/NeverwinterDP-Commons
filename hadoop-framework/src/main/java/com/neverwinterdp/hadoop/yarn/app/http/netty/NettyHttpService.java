package com.neverwinterdp.hadoop.yarn.app.http.netty;

import java.net.UnknownHostException;

import com.neverwinterdp.hadoop.yarn.app.http.HttpService;
import com.neverwinterdp.hadoop.yarn.app.http.netty.rest.AppRestRouteHandler;
import com.neverwinterdp.hadoop.yarn.app.http.netty.webapp.InfoRouteHandler;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.netty.http.HttpServer;

public class NettyHttpService implements HttpService {
  private HttpServer server ;
  private Thread serviceThread ;
  
  public NettyHttpService(AppMaster appMaster) throws Exception {
    this(appMaster, 0);
  }
  
  public NettyHttpService(AppMaster appMaster, int port) throws Exception {
    server = new HttpServer();
    server.setPort(port) ;
    InfoRouteHandler infoHandler = new InfoRouteHandler(appMaster) ;
    server.add("/"    ,         infoHandler);
    server.add("/info",         infoHandler);
    server.add("/info/:view",   infoHandler);
    server.add("/info/container/:id",   infoHandler);
    server.add("/rest/:res",  new AppRestRouteHandler(appMaster));
  }
  
  public String getListenIPAddress() {
    try {
      return server.getHostIpAddress() ;
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    return null ;
  }
  
  public int getListenPort() { 
    return server.getPort() ;
  }
  
  public String getTrackingUrl() {
    return "http://" + getListenIPAddress() + ":" + getListenPort() ;
  }
  
  public void start() throws Exception {
    serviceThread = new Thread() {
      public void run() {
        try {
          server.start() ;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    serviceThread.start();
  }
  
  public void startAsDeamon() throws Exception {
    serviceThread = new Thread() {
      public void run() {
        try {
          server.startAsDeamon(); ;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    serviceThread.start();
  }
  
  public void shutdown() {
    if(serviceThread  != null) {
      serviceThread.interrupt();
      server.shutdown(); 
    }
  }
}
