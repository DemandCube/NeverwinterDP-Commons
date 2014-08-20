package com.neverwinterdp.hadoop.yarn.app.http.netty.webapp;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerInfo;
import com.neverwinterdp.netty.http.RouteMatcher;
import com.neverwinterdp.netty.http.webapp.WebAppRouteHandler;

public class InfoRouteHandler extends WebAppRouteHandler {
  private RouteMatcher<SubRouteHandler> subRouteHandlers = new RouteMatcher<SubRouteHandler>() ;
  private AppMaster appMaster ;
  private InfoPage infoPage ;
  private String baseUrl ;
  
  public InfoRouteHandler(AppMaster appMaster) throws Exception {
    this.appMaster = appMaster ;
    baseUrl = "/proxy/" + appMaster.getAppInfo().appId ;
    subRouteHandlers.setDefaultHandler(new AppConfigHandler());
    subRouteHandlers.addPattern("/info/config", subRouteHandlers.getDefaultHandler());
    subRouteHandlers.addPattern("/info/monitor", new MonitorHandler());
    subRouteHandlers.addPattern("/info/container/:id", new ContainerHandler());
  }
  
  protected void process(Writer writer, FullHttpRequest request) throws Exception {
    infoPage = new InfoPage() ;
    QueryStringDecoder uriDecoder = new QueryStringDecoder(request.getUri()) ;
    SubRouteHandler handler = subRouteHandlers.findHandler(uriDecoder.path()) ;
    Map<String, Object> scopes = new HashMap<String, Object>() ;
    scopes.put("baseUrl", baseUrl) ;
    handler.process(infoPage, scopes, uriDecoder);
    infoPage.render(writer, scopes);
  }

  public interface SubRouteHandler {
    void process(InfoPage page, Map<String, Object> scopes, QueryStringDecoder uriDecoder) throws Exception ;
  }
  
  public class AppConfigHandler implements SubRouteHandler {
    public void process(InfoPage page, Map<String, Object> scopes, QueryStringDecoder uriDecoder) throws Exception {
      scopes.put("appConfig", appMaster.getAppInfo()) ;
      page.setAppConfigView();
    }
  }
  
  public class MonitorHandler implements SubRouteHandler {
    public void process(InfoPage page, Map<String, Object> scopes, QueryStringDecoder uriDecoder) throws Exception {
      scopes.put("appMonitor", appMaster.getAppMonitor()) ;
      page.setMonitorView();
    }
  }
  
  public class ContainerHandler implements SubRouteHandler {
    public void process(InfoPage page, Map<String, Object> scopes, QueryStringDecoder uriDecoder) throws Exception {
      String path = uriDecoder.path() ;
      String id = path.substring(path.lastIndexOf("/") + 1) ;
      int containerId = Integer.parseInt(id) ;
      AppWorkerContainerInfo containerInfo = appMaster.getAppMonitor().getContainerInfo(containerId) ;
      scopes.put("containerInfo", containerInfo) ;
      page.setContainerView();
    }
  }
}
