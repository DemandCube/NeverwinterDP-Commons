package com.neverwinterdp.netty.http;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neverwinterdp.netty.http.client.DumpResponseHandler;
import com.neverwinterdp.netty.http.client.HttpClient;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class StaticFileHandlerUnitTest {
  private HttpServer server ;
  
  @Before
  public void setup() throws Exception {
    Map<String, String> props = new HashMap<String, String>() ;
    props.put("port","8080") ;
    props.put("www-dir", ".") ;
    server = new HttpServer() ;
    server.configure(props);
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
