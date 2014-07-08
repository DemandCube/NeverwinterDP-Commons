package com.neverwinterdp.server.gateway.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

import com.neverwinterdp.netty.http.route.RouteHandlerGeneric;
import com.neverwinterdp.server.gateway.ClusterGateway;
import com.neverwinterdp.util.JSONSerializer;

public class HttpGatewayRouteHandler extends RouteHandlerGeneric {
  private ClusterGateway cluster ;
  
  public HttpGatewayRouteHandler() {
    cluster = new ClusterGateway() ;
  }
  
  public HttpGatewayRouteHandler(String ... connect) {
    cluster = new ClusterGateway(connect) ;
  }
  
  protected void doPost(ChannelHandlerContext ctx, HttpRequest request) {
    FullHttpRequest req = (FullHttpRequest) request ;
    ByteBuf byteBuf = req.content() ;
    byte[] bytes = new byte[byteBuf.readableBytes()] ;
    byteBuf.readBytes(bytes) ;
    HttpGatewayRequest grequest = JSONSerializer.INSTANCE.fromBytes(bytes, HttpGatewayRequest.class) ;
    HttpGatewayResponse gresponse = new HttpGatewayResponse(grequest) ;
    gresponse.setData(cluster.execute(grequest.getGroup(), grequest.getCommand(), grequest.getParams())) ;
    System.out.println("Response data = " + gresponse.getData()) ;
    writeJSON(ctx, request, gresponse);
  }
}
