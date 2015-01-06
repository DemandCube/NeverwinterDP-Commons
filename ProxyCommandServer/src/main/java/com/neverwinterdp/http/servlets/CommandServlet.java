package com.neverwinterdp.http.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class CommandServlet extends HttpServlet {
  public static String noCommandMessage = "No Command Sent";
  
  @Override
  protected void doGet(HttpServletRequest request,
                       HttpServletResponse response ) throws ServletException,IOException{
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().print("Hello from commandServlet");
  }
  
  
  @Override 
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    
    String command = request.getParameter("command");
    
    
    if(command == null){
      response.getWriter().print(noCommandMessage);
    }
    else{
      response.getWriter().print("command run: "+command);
    }
  }
}
