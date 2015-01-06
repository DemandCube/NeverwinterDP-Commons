package com.neverwinterdp.http;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import com.neverwinterdp.http.servlets.CommandServlet;

public class HttpServer {
  private static Server server = null;
  
  public HttpServer(){
    this(8080, new CommandServlet());
  }
  
  public HttpServer(int port){
    this(port, new CommandServlet());
  }
  
  public HttpServer(int port, HttpServlet handler){
    server = new Server(port);
    ServletHandler s = new ServletHandler();
    server.setHandler(s);
    s.addServletWithMapping(handler.getClass(), "/*");
  }
  
  public void run() throws Exception{
    server.start();
  }
  
  public void join() throws Exception{
    // The use of server.join() the will make the current thread join and
    // wait until the server is done executing.
    // See
    // http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join()
    server.join();
  }
    
  public void stop() throws Exception{
    server.stop();
  }

  public static void main( String[] args ) throws Exception{
    HttpServer s = new HttpServer(8080);
    s.run();
    s.join();
  }

  
}
