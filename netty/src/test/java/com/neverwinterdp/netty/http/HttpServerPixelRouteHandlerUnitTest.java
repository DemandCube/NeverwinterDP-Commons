package com.neverwinterdp.netty.http;

import static org.junit.Assert.assertEquals;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neverwinterdp.netty.http.client.AsyncHttpClient;
import com.neverwinterdp.netty.http.client.DumpResponseHandler;
import com.neverwinterdp.netty.http.client.ResponseHandler;

/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class HttpServerPixelRouteHandlerUnitTest {
  private HttpServer server ;
  
  @Before
  public void setup() throws Exception {
    server = new HttpServer();
    server.add("/pixel", new PixelRouteHandler());
    new Thread() {
      public void run() {
        try {
          server.start() ;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }.start() ;
    Thread.sleep(1000);
  }
  
  @After
  public void teardown() {
    server.shutdown() ;
  }

  @Test
  public void testContentReturnedMatchesContentServed() throws Exception {
    DumpResponseHandler handler = new DumpResponseHandler() ;
    AsyncHttpClient client = new AsyncHttpClient ("127.0.0.1", 8080, handler) ;
    client.get("/pixel");
    
    //TODO: 1000 or 500 should be enough, You should try to reason and estimate the needed resource. Save every second
    Thread.sleep(3000);
    
    assertEquals(handler.getContent(), PixelRouteHandler.IMAGE.toString(CharsetUtil.UTF_8)) ;
    //TODO: Need to release resource after using. client close or shutdown
  }

  @Test
  public void testContentReturnedMatchesContentServedAfter100Requests() throws Exception {
    DumpResponseHandler handler = new DumpResponseHandler() ;
    AsyncHttpClient client = new AsyncHttpClient ("127.0.0.1", 8080, handler) ;
    for(int i = 0; i < 100; i++) {
      client.get("/pixel");
    }
    Thread.sleep(1000);
    assertEquals(100, handler.getCount()) ;
    
    
    client.get("/pixel");
    //TODO: You send 100 request and expect it processes in 1s while sending 1 request and expect it process in 3s
    Thread.sleep(3000);
    
    assertEquals(handler.getContent(), PixelRouteHandler.IMAGE.toString(CharsetUtil.UTF_8)) ;
    //TODO: release resource
  }

  //TODO: You should not change my handler to serve solve your assert problem
  static public class PixelCheckResponseHandler implements ResponseHandler {
    int count ; 
    public void onResponse(HttpResponse response) {
      count++ ;
      HttpContent content = (HttpContent) response;
      ByteBuf buf = content.content() ;
      assertEquals(buf, PixelRouteHandler.IMAGE) ;
    }
  }
}
