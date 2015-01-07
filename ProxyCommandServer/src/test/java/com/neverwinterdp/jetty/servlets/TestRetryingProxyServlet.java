package com.neverwinterdp.jetty.servlets;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.neverwinterdp.jetty.JettyServer;
import com.neverwinterdp.jetty.servlets.HelloServlet;
import com.neverwinterdp.jetty.servlets.RetryingProxyServlet;

public class TestRetryingProxyServlet {
  private static JettyServer httpServer;
  private static int httpPort = 8181;
  private static int proxyPort = 8282;
  
  @BeforeClass
  public static void setup() throws Exception{
    httpServer = new JettyServer(httpPort, new HelloServlet());
    httpServer.run();
  }
  
  @AfterClass
  public static void teardown() throws Exception{
    httpServer.stop();
  }
  
  @Test
  public void testRetryingProxyServer() throws Exception{
    JettyServer proxyServer = null;
    try{
      proxyServer = new JettyServer(proxyPort,
          new RetryingProxyServlet("http://localhost:"+Integer.toString(httpPort)));
      proxyServer.run();
      
      //Make sure http server is fine first, just for sanity's sake
      HttpResponse<String> httpResp = Unirest.get("http://localhost:"+Integer.toString(httpPort)).asString();
      assertEquals(HelloServlet.responseString, httpResp.getBody());
      assertEquals(200, httpResp.getCode());
      
      
      //Test proxy
      HttpResponse<String> proxyResp = Unirest.get("http://localhost:"+Integer.toString(proxyPort)).asString();
      assertEquals(HelloServlet.responseString, proxyResp.getBody());
      //assertEquals(200, proxyResp.getCode());
    }
    finally{
      proxyServer.stop();
    }
    
    
  }
}
