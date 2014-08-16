package com.neverwinterdp.hadoop.yarn.app.http.netty.rest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import com.neverwinterdp.netty.http.rest.RestRouteHandler;

public class AppMonitorRouteHandler extends RestRouteHandler {
  protected Object get(ChannelHandlerContext ctx, FullHttpRequest request) {
    return "HelloHandler Get" ;
  }
}
