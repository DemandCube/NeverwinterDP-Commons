package com.neverwinterdp.hadoop.yarn.app.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import com.neverwinterdp.netty.http.RouteHandlerGeneric;
import com.neverwinterdp.util.JSONSerializer;

public class RouteAppRestHandler extends RouteHandlerGeneric {
  protected void doPost(ChannelHandlerContext ctx, HttpRequest request) {
    try {
      FullHttpRequest req = (FullHttpRequest) request ;
      String uri = req.getUri() ;
      QueryStringDecoder reqDecoder = new QueryStringDecoder("/hello?recipient=world&x=1;y=2");
      assert reqDecoder.path().equals("/hello");
      assert reqDecoder.parameters().get("recipient").get(0).equals("world");
      assert reqDecoder.parameters().get("x").get(0).equals("1");
      assert reqDecoder.parameters().get("y").get(0).equals("2");
      
      ByteBuf byteBuf = req.content() ;
      byte[] bytes = new byte[byteBuf.readableBytes()] ;
      byteBuf.readBytes(bytes) ;
      RestRequest rRequest = JSONSerializer.INSTANCE.fromBytes(bytes, RestRequest.class) ;
      RestResponse rResponse = new RestResponse(rRequest) ;
      writeJSON(ctx, request, rResponse);
    } catch(Throwable t) {
      RestResponse gresponse = new RestResponse() ;
      gresponse.setData(t.getMessage());
      writeJSON(ctx, request, gresponse);
      t.printStackTrace(); 
    }
  }
}
