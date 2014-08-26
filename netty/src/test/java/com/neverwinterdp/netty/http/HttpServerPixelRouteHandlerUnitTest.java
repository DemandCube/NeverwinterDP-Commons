package com.neverwinterdp.netty.http;

import static org.junit.Assert.assertEquals;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neverwinterdp.netty.http.client.AsyncHttpClient;
import com.neverwinterdp.netty.http.client.ResponseHandler;

/**
 * @author Richard Duarte
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
    PixelCheckResponseHandler handler = new PixelCheckResponseHandler();
    AsyncHttpClient client = new AsyncHttpClient ("127.0.0.1", 8080, handler) ;
    client.get("/pixel");
    Thread.sleep(500);
    //Make sure 1 response has been received
    assertEquals(1,handler.getCount());
    client.close();
  }

  @Test
  public void testContentReturnedMatchesContentServed100Requests() throws Exception {
    PixelCheckResponseHandler handler = new PixelCheckResponseHandler();
    AsyncHttpClient client = new AsyncHttpClient ("127.0.0.1", 8080, handler) ;
    for(int i = 0; i < 100; i++) {
      client.get("/pixel");
    }
    Thread.sleep(1000);
    //Make sure 100 responses have been received
    assertEquals(100, handler.getCount());
    client.close();
  }

  /**
   * Handler to make sure when HTTP response is received,
   * it matches the content served from PixelRouteHandler
   */
  static public class PixelCheckResponseHandler implements ResponseHandler {
    int count=0; 
    public void onResponse(HttpResponse response) {
      count++;
      HttpContent content = (HttpContent) response;
      ByteBuf buf = content.content();
      assertEquals(buf, PixelRouteHandler.IMAGE);
    }
    public int getCount(){
      return this.count;
    }
  }
}
