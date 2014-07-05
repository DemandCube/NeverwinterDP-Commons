package com.neverwinterdp.netty.http;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neverwinterdp.netty.http.client.DumpResponseHandler;
import com.neverwinterdp.netty.http.client.HttpClient;
import com.neverwinterdp.netty.http.route.StaticFileHandler;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class SparknginHttpServerUnitTest {
  private HttpServer server ;
  
  @Before
  public void setup() throws Exception {
    server = new HttpServer() ;
    server.setDefault(new StaticFileHandler(".")) ;
    server.startAsDeamon() ;
    Thread.sleep(2000) ;
  }
  
  @After
  public void teardown() {
    server.shutdown() ;
  }
  
  @Test
  public void testStaticFileHandler() throws Exception {
    DumpResponseHandler handler = new DumpResponseHandler() ;
    HttpClient client = new HttpClient ("127.0.0.1", 8080, handler) ;
    client.get("/build.gradle");
    Thread.sleep(100) ;
  }
}
