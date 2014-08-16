package com.neverwinterdp.hadoop.yarn.app.http.webapp;

import java.net.InetSocketAddress;

import org.apache.hadoop.http.HttpConfig.Policy;
import org.apache.hadoop.yarn.webapp.WebApp;
import org.apache.hadoop.yarn.webapp.WebApps;

import com.neverwinterdp.hadoop.yarn.app.http.HttpService;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;

public class YarnHttpService implements HttpService {
  private AppMaster appMaster ;
  private WebApp webApp ;
  
  public YarnHttpService(AppMaster appMaster) {
    this.appMaster = appMaster ;
  }
  
  public String getTrackingUrl() {
    InetSocketAddress listenAddr = webApp.getListenerAddress() ;
    return "http://" + listenAddr.getAddress().getHostAddress() + ":" + webApp.port()  ;
  }

  public void start() throws Exception {
    webApp =
        WebApps.
          $for("webui", null, null, "ws").
          withHttpPolicy(appMaster.getConfiguration(), Policy.HTTP_ONLY).start(new AMWebApp(appMaster));
  }

  public void shutdown() {
    if(webApp != null) {
      webApp.stop() ;
      webApp = null ;
    }
  }

}
