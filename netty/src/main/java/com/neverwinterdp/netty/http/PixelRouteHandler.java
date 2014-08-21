package com.neverwinterdp.netty.http;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author Richard Duarte
 */
public class PixelRouteHandler extends RouteHandlerGeneric {
  
  //This is a byte array for a 1x1 100% transparent .png image 
  final ByteBuf imgBuf = Unpooled.wrappedBuffer(
                    new byte[]
                    {(byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 
                      0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52, 
                      0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
                      0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte)0xC4, 
                      (byte)0x89, 0x00, 0x00, 0x00, 0x06, 0x62, 0x4B, 0x47,
                      0x44, 0x00, (byte)0xFF, 0x00, (byte)0xFF, 0x00, (byte)0xFF, (byte)0xA0,
                      (byte)0xBD, (byte)0xA7, (byte)0x93, 0x00, 0x00, 0x00, 0x09, 0x70, 
                      0x48, 0x59, 0x73, 0x00, 0x00, 0x0B, 0x13, 0x00,
                      0x00, 0x0B, 0x13, 0x01, 0x00, (byte)0x9A, (byte)0x9C, 0x18,
                      0x00, 0x00, 0x00, 0x07, 0x74, 0x49, 0x4D, 0x45,
                      0x07, (byte)0xDE, 0x08, 0x14, 0x14, 0x24, 0x12, 0x12,
                      (byte)0x95, (byte)0xC7, (byte)0xB4, 0x00, 0x00, 0x00, 0x0C, 0x69,
                      0x54, 0x58, 0x74, 0x43, 0x6F, 0x6D, 0x6D, 0x65,
                      0x6E, 0x74, 0x00, 0x00, 0x00, 0x00, 0x00, (byte)0xBC,
                      (byte)0xAE, (byte)0xB2, (byte)0x99, 0x00, 0x00, 0x00, 0x10, 0x49,
                      0x44, 0x41, 0x54, 0x08, 0x1D, 0x01, 0x05, 0x00,
                      (byte)0xFA, (byte)0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                      0x05, 0x00, 0x01, (byte)0xBA, (byte)0x89, 0x10, (byte)0x8A, 0x00,
                      0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, (byte)0xAE,
                      0x42, 0x60, (byte)0x82});
  
  protected void writeContent(ChannelHandlerContext ctx, HttpRequest req, ByteBuf content, String contentType){
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, imgBuf);
    
    
    response.headers().set(CONTENT_TYPE, "image/png");
    response.headers().set(CONTENT_LENGTH, imgBuf.capacity());
    
    boolean keepAlive = isKeepAlive(req);
    if (!keepAlive) {
      ctx.write(response).addListener(ChannelFutureListener.CLOSE);
    } else {
      response.headers().set(CONNECTION, Values.KEEP_ALIVE);
      ctx.write(response);
    }
    ctx.flush() ;
  }
  
}