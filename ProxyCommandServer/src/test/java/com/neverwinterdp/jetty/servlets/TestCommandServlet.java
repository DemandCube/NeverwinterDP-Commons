package com.neverwinterdp.jetty.servlets;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.neverwinterdp.jetty.JettyServer;
import com.neverwinterdp.jetty.servlets.CommandServlet;

public class TestCommandServlet {
  private static JettyServer httpServer;
  private static int port = 8181;
  private static String testCommand = "testcommand";
  
  @BeforeClass
  public static void setup() throws Exception{
    httpServer = new JettyServer(port, new CommandServlet());
    httpServer.run();
  }
  
  @AfterClass
  public static void teardown() throws Exception{
    httpServer.stop();
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
