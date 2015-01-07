package com.neverwinterdp.jetty.servlets;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.proxy.ProxyServlet;

@SuppressWarnings("serial")
public class RetryingProxyServlet extends ProxyServlet{
  private String forwardUrl;
  
  public RetryingProxyServlet(String forwardUrl){
    this.forwardUrl = forwardUrl;
  }
  
  
  @Override
  protected URI rewriteURI(HttpServletRequest request) {
    //String query = request.getQueryString();
    
    System.out.println("WTF?");
    URI x = URI.create(this.forwardUrl);
    System.out.println(x.toString());
    return x.normalize();
    //return URI.create(this.forwardUrl);
  }
}
