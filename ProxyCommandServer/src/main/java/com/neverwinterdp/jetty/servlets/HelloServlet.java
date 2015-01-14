package com.neverwinterdp.jetty.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class HelloServlet extends HttpServlet{
  public static String responseString = "Hello from HelloServlet";
  
  @Override
  protected void doGet( HttpServletRequest request,
                        HttpServletResponse response ) throws ServletException,IOException{
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().print(responseString);
  }
}