package com.neverwinterdp.hadoop.yarn.app.history;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.hubspot.jackson.datatype.protobuf.ProtobufModule;
import com.neverwinterdp.hadoop.yarn.app.AppConfig;
import com.neverwinterdp.netty.http.RouteMatcher;
import com.neverwinterdp.netty.http.rest.RestRouteHandler;
import com.neverwinterdp.util.ExceptionUtil;
import com.neverwinterdp.util.JSONSerializer;

public class AppHistoryRouteHandler extends RestRouteHandler {
  private Map<String, AppHistory> histories = new LinkedHashMap<String, AppHistory>() ;
  private RouteMatcher<SubRouteHandler> subRouteHandlers = new RouteMatcher<SubRouteHandler>() ;
  
  public AppHistoryRouteHandler() throws Exception {
    setJSONSerializer(new JSONSerializer(new ProtobufModule())) ;
    subRouteHandlers.addPattern("/.*/list", new ListAppHandler());
    subRouteHandlers.addPattern("/.*/app/:id", new AppHandler());
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
  
  protected Object post(ChannelHandlerContext ctx, FullHttpRequest request) {
    byte[] data = getBodyData(request) ;
    AppHistory appHistory = jsonSerializer.fromBytes(data, AppHistory.class) ;
    histories.put(appHistory.getAppConfig().appId, appHistory) ;
    return new AppHistorySendAck(true);
  }
  
  public interface SubRouteHandler {
    Object process(QueryStringDecoder uriDecoder) throws Exception ;
  }
  
  public class ListAppHandler implements SubRouteHandler {
    public Object process(QueryStringDecoder uriDecoder) throws Exception {
      Iterator<AppHistory> i = histories.values().iterator() ;
      List<AppHistoryDescription> holder = new ArrayList<AppHistoryDescription>() ;
      while(i.hasNext()) {
        AppHistory history = i.next() ;
        AppConfig appInfo = history.getAppConfig() ;
        AppHistoryDescription desc = new AppHistoryDescription() ;
        desc.setAppId(appInfo.appId);
        desc.setStartTime(new Date(appInfo.appStartTime).toString());
        desc.setFinishTime(new Date(appInfo.appFinishTime).toString());
        desc.setState(appInfo.appState) ;
        holder.add(desc) ;
      }
      return holder ;
    }
  }
  
  public class AppHandler implements SubRouteHandler {
    public Object process(QueryStringDecoder uriDecoder) throws Exception {
      String path = uriDecoder.path(); 
      String appId = path.substring(path.lastIndexOf("/") + 1) ;
      return histories.get(appId) ;
    }
  }
}
