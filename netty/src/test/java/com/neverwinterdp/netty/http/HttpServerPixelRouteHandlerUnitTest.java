package com.neverwinterdp.netty.http;

import static org.junit.Assert.assertEquals;
import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neverwinterdp.netty.http.client.AsyncHttpClient;
import com.neverwinterdp.netty.http.client.DumpResponseHandler;

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
    
    Thread.sleep(3000);
    
    assertEquals(handler.getContent(), PixelRouteHandler.IMAGE.toString(CharsetUtil.UTF_8)) ;
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
    
    Thread.sleep(3000);
    
    assertEquals(handler.getContent(), PixelRouteHandler.IMAGE.toString(CharsetUtil.UTF_8)) ;
  }
}
