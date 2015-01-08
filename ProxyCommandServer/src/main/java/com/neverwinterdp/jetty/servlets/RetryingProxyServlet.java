package com.neverwinterdp.jetty.servlets;

import java.net.URI;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.proxy.ProxyServlet;

@SuppressWarnings("serial")
public class RetryingProxyServlet extends ProxyServlet {
  private String forwardUrl;
  
  
  //CANNOT create a constructor with arguments because of the way
  //that the server instantiates these objects
  //public RetryingProxyServlet(String forwardUrl){
  //  this.forwardUrl = forwardUrl;
  //}
  
  public void setForwardingUrl(String forwardUrl){
    this.forwardUrl = forwardUrl;
  }
  
  @Override
  public void init() throws ServletException {
    super.init();
    ServletConfig conf = this.getServletConfig();
    this.forwardUrl = conf.getInitParameter("proxyTo");
    if(this.forwardUrl == null){
      System.err.println("Forwarding URL is NULL!  Please edit your web.xml");
    }
  }
    
  @Override
  protected URI rewriteURI(HttpServletRequest request) {
    return URI.create(this.forwardUrl);
  }
  
  //No longer need to override
  //Add this to web.xml instead:
//  <servlet>
//  <servlet-name>ProxyServlet</servlet-name>
//  <servlet-class>com.neverwinterdp.jetty.servlets.RetryingProxyServlet</servlet-class>
//  <init-param>
//    <param-name>proxyTo</param-name>
//    <param-value>http://localhost:8181</param-value>
//  </init-param>
//  <init-param>
//    <param-name>org.eclipse.jetty.server.Executor</param-name>
//    <param-value>org.eclipse.jetty.util.thread.QueuedThreadPool</param-value>
//  </init-param>
//  <load-on-startup>1</load-on-startup>
//  <async-supported>true</async-supported>
//  </servlet>
  /*
  @Override
  protected HttpClient createHttpClient() throws ServletException{
    ServletConfig config = getServletConfig();

    HttpClient client = newHttpClient();
    
    // Redirects must be proxied as is, not followed
    client.setFollowRedirects(false);

    // Must not store cookies, otherwise cookies of different clients will mix
    client.setCookieStore(new HttpCookieStore.Empty());

    Executor executor;
    String value = config.getInitParameter("maxThreads");
    if (value == null || "-".equals(value))
    {
      executor = new QueuedThreadPool(100, 10);
      //executor = (Executor)getServletContext().getAttribute("org.eclipse.jetty.server.Executor");
      //if (executor==null)
      //    throw new IllegalStateException("No server executor for proxy");
    }
    else
    {
        QueuedThreadPool qtp= new QueuedThreadPool(Integer.parseInt(value));
        String servletName = config.getServletName();
        int dot = servletName.lastIndexOf('.');
        if (dot >= 0)
            servletName = servletName.substring(dot + 1);
        qtp.setName(servletName);
        executor=qtp;
    }
    
    client.setExecutor(executor);

    value = config.getInitParameter("maxConnections");
    if (value == null)
        value = "256";
    client.setMaxConnectionsPerDestination(Integer.parseInt(value));

    value = config.getInitParameter("idleTimeout");
    if (value == null)
        value = "30000";
    client.setIdleTimeout(Long.parseLong(value));

    value = config.getInitParameter("timeout");
    if (value == null)
        value = "60000";
    //this._timeout = Long.parseLong(value);
    this.setTimeout(Long.parseLong(value));

    value = config.getInitParameter("requestBufferSize");
    if (value != null)
        client.setRequestBufferSize(Integer.parseInt(value));

    value = config.getInitParameter("responseBufferSize");
    if (value != null)
        client.setResponseBufferSize(Integer.parseInt(value));

    try
    {
        client.start();

        // Content must not be decoded, otherwise the client gets confused
        client.getContentDecoderFactories().clear();

        return client;
    }
    catch (Exception x)
    {
        throw new ServletException(x);
    }
  }
  */
}


