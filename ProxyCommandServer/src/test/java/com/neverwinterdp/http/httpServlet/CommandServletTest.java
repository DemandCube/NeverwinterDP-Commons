package com.neverwinterdp.http.httpServlet;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.neverwinterdp.http.HttpServer;
import com.neverwinterdp.http.servlets.CommandServlet;

public class CommandServletTest {
  private static HttpServer ps;
  private static int port = 8181;
  private static String testCommand = "testcommand";
  
  @BeforeClass
  public static void setup() throws Exception{
    ps = new HttpServer(port, new CommandServlet());
    ps.run();
  }
  
  @AfterClass
  public static void teardown() throws Exception{
    ps.stop();
  }
  
  @Test
  public void testCommandServlet() throws InterruptedException, UnirestException{
    HttpResponse<String> resp = Unirest.post("http://localhost:"+Integer.toString(port))
           .field("command", testCommand)
           .asString();
    
    assertEquals("command run: "+testCommand, resp.getBody());
  }
  
  @Test
  public void testNoCommand() throws UnirestException{
    HttpResponse<String> resp = Unirest.post("http://localhost:"+Integer.toString(port))
        .asString();
    
    assertEquals(CommandServlet.noCommandMessage, resp.getBody());
  }
}
