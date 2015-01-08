package com.neverwinterdp.jetty;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.neverwinterdp.jetty.JettyServer;
import com.neverwinterdp.jetty.servlets.HelloServlet;

public class TestJettyServer {
  private static JettyServer httpServer;
  private static int port = 8181;
  
  @BeforeClass
  public static void setup() throws Exception{
    httpServer = new JettyServer(port, HelloServlet.class);
    httpServer.run();
  }
  
  @AfterClass
  public static void teardown() throws Exception{
    httpServer.stop();
  }
  
  @Test
  public void testHTTPServer() throws InterruptedException, UnirestException{
    HttpResponse<String> resp = Unirest.get("http://localhost:"+Integer.toString(port)).asString();
    
    assertEquals(HelloServlet.responseString, resp.getBody());
    assertEquals(200, resp.getCode());
    
    Map<String,String> expectedHeaders = new HashMap<String,String>();
    expectedHeaders.put("content-type", "text/html; charset=ISO-8859-1");
    expectedHeaders.put("content-length", Integer.toString(HelloServlet.responseString.length()));
    expectedHeaders.put("server", "Jetty(9.2.0.RC0)");
    expectedHeaders.put("date", new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z").format(new Date()));
    assertEquals(expectedHeaders, resp.getHeaders());
  }
}
