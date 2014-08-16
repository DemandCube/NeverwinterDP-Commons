package com.neverwinterdp.hadoop.yarn.app.http.netty.webapp;

import io.netty.handler.codec.http.FullHttpRequest;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.neverwinterdp.hadoop.yarn.app.AppConfig;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.netty.http.webapp.WebAppRouteHandler;

public class InfoPageHandler extends WebAppRouteHandler {
  private AppConfig appConfig ;
  private InfoPage infoPage ;
  
  public InfoPageHandler(AppMaster appMaster) throws Exception {
    this.appConfig = appMaster.getConfig() ;
  }
  
  protected void process(Writer writer, FullHttpRequest request) throws Exception {
    Map<String, Object> scopes = new HashMap<String, Object>() ;
    scopes.put("appConfig", appConfig) ;
    infoPage = new InfoPage() ;
    infoPage.render(writer, scopes);
  }

}
