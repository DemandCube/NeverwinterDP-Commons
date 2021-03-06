package com.neverwinterdp.jetty;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.webapp.WebAppContext;

import com.neverwinterdp.jetty.servlets.HelloServlet;

public class JettyServer {
  protected Server server = null;
  
  public JettyServer() {
    this(8080, HelloServlet.class);
  }
  
  public JettyServer(int port) {
    this(port, HelloServlet.class);
  }
  
  public JettyServer(int port, Class<?extends Servlet> servletClass) {
    server = new Server(port);
    ServletHandler s = new ServletHandler();
    server.setHandler(s);
    s.addServletWithMapping(servletClass, "/*");
  }
  
  public void setHandler(WebAppContext w){
    server.setHandler(w);
  }
  
  public void start() throws Exception{
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
    JettyServer s = new JettyServer(8080);
    s.start();
    s.join();
  }

  
}
