package com.neverwinterdp.proxy;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.neverwinterdp.http.HttpServer;

public class ProxyServerTest {
  private static HttpServer ps;
  private static int port = 8181;
  private static String testResponse = "Test Response!";
  
  @BeforeClass
  public static void setup() throws Exception{
    ps = new HttpServer(port, new testServlet());
    ps.run();
  }
  
  @AfterClass
  public static void teardown() throws Exception{
    ps.stop();
  }
  
  @Test
  public void testProxyServer() throws InterruptedException, UnirestException{
    HttpResponse<String> resp = Unirest.get("http://localhost:"+Integer.toString(port)).asString();
    
    assertEquals(testResponse, resp.getBody());
    assertEquals(200, resp.getCode());
    
    Map<String,String> expectedHeaders = new HashMap<String,String>();
    expectedHeaders.put("content-type", "text/html; charset=ISO-8859-1");
    expectedHeaders.put("content-length", Integer.toString(testResponse.length()));
    expectedHeaders.put("server", "Jetty(9.0.0.RC2)");
    assertEquals(expectedHeaders, resp.getHeaders());

  }
  
  @SuppressWarnings("serial")
  public static class testServlet extends HttpServlet{
    @Override
    protected void doGet( HttpServletRequest request,
                          HttpServletResponse response ) throws ServletException,IOException{
      response.setContentType("text/html");
      response.setStatus(HttpServletResponse.SC_OK);
      response.getWriter().print(testResponse);
    }
  }
}
