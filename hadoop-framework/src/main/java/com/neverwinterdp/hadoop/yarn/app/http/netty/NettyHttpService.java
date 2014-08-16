package com.neverwinterdp.hadoop.yarn.app.http.netty;

import java.net.UnknownHostException;

import com.neverwinterdp.hadoop.yarn.app.http.HttpService;
import com.neverwinterdp.hadoop.yarn.app.http.netty.rest.AppConfigRouteHandler;
import com.neverwinterdp.hadoop.yarn.app.http.netty.rest.AppMonitorRouteHandler;
import com.neverwinterdp.hadoop.yarn.app.http.netty.webapp.InfoPageHandler;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.netty.http.HttpServer;

public class NettyHttpService implements HttpService {
  private HttpServer server ;
  
  public NettyHttpService(AppMaster appMaster) throws Exception {
    server = new HttpServer();
    server.setPort(0) ;
    InfoPageHandler defaultHandler = new InfoPageHandler(appMaster) ;
    server.add("/"    ,         defaultHandler);
    server.add("/info",         defaultHandler);
    server.add("/rest/config",  new AppConfigRouteHandler());
    server.add("/rest/monitor", new AppMonitorRouteHandler());
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
    new Thread() {
      public void run() {
        try {
          server.start() ;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }.start();
  }
  
  public void startAsDeamon() throws Exception {
    new Thread() {
      public void run() {
        try {
          server.startAsDeamon(); ;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }.start();
  }
  
  public void shutdown() {
    server.shutdown(); 
  }
}
