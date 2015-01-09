package com.neverwinterdp.jetty.servlets;

import static org.junit.Assert.assertEquals;

import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.neverwinterdp.jetty.JettyServer;


public class TestRetryingProxyServlet {
  private static JettyServer httpServer;
  private static int httpPort = 8181;
  private static int proxyPort = 8282;
  
  @BeforeClass
  public static void setup() throws Exception{
    httpServer = new JettyServer(httpPort, HelloServlet.class);
    httpServer.start();
  }
  
  @AfterClass
  public static void teardown() throws Exception{
    httpServer.stop();
  }
  
  @Test
  public void testRetryingProxyServer() throws Exception{
    //Used to point to custom web.xml
    WebAppContext webapp = new WebAppContext();
    webapp.setResourceBase("./src/test/resources/");
    webapp.setDescriptor("./src/test/resources/override-web.xml");
    
    
    JettyServer proxyServer = null;
    try{
      proxyServer = new JettyServer(proxyPort, RetryingProxyServlet.class);proxyServer.setHandler(webapp);
      proxyServer.start();
      
      //Make sure http server is fine first, just for sanity's sake
      HttpResponse<String> httpResp = Unirest.get("http://localhost:"+Integer.toString(httpPort)).asString();
      assertEquals(HelloServlet.responseString, httpResp.getBody());
      assertEquals(200, httpResp.getCode());
      
      //Test proxy
      HttpResponse<String> proxyResp = Unirest.get("http://localhost:"+Integer.toString(proxyPort)).asString();
      assertEquals(HelloServlet.responseString, proxyResp.getBody());
      //assertEquals(200, proxyResp.getCode());
    }
    catch(Exception e){
      e.printStackTrace();
    }
    finally{
      proxyServer.stop();
    } 
  }
}
