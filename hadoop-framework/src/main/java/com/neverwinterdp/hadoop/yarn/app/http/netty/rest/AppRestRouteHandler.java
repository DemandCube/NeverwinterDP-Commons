package com.neverwinterdp.hadoop.yarn.app.http.netty.rest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.netty.http.RouteMatcher;
import com.neverwinterdp.netty.http.rest.RestRouteHandler;
import com.neverwinterdp.util.ExceptionUtil;

public class AppRestRouteHandler extends RestRouteHandler {
  private RouteMatcher<SubRouteHandler> subRouteHandlers = new RouteMatcher<SubRouteHandler>() ;
  private AppMaster appMaster ;
  
  public AppRestRouteHandler(AppMaster appMaster) throws Exception {
    this.appMaster = appMaster ;
    subRouteHandlers.setDefaultHandler(new AppConfigHandler());
    subRouteHandlers.addPattern("/rest/config", subRouteHandlers.getDefaultHandler());
    subRouteHandlers.addPattern("/rest/monitor", new MonitorHandler());
  }
  
  protected Object get(ChannelHandlerContext ctx, FullHttpRequest request) {
    QueryStringDecoder uriDecoder = new QueryStringDecoder(request.getUri()) ;
    SubRouteHandler handler = subRouteHandlers.findHandler(uriDecoder.path()) ;
    try {
      if(handler != null) return handler.process(uriDecoder) ;
      return null ;
    } catch (Exception e) {
      return ExceptionUtil.getStackTrace(e) ;
    }
  }

  public interface SubRouteHandler {
    Object process(QueryStringDecoder uriDecoder) throws Exception ;
  }
  
  public class AppConfigHandler implements SubRouteHandler {
    public Object process(QueryStringDecoder uriDecoder) throws Exception {
      return appMaster.getAppInfo() ;
    }
  }
  
  public class MonitorHandler implements SubRouteHandler {
    public Object process(QueryStringDecoder uriDecoder) throws Exception {
      return appMaster.getAppInfo() ;
    }
  }
}
