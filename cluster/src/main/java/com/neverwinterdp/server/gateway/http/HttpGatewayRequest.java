package com.neverwinterdp.server.gateway.http;


public class HttpGatewayRequest {
  private String command ;
  
  public HttpGatewayRequest() {} 
  
  public HttpGatewayRequest(String command) {
    this.command = command ;
  }
  
  
  public String getCommand() { return command; }
  public void setCommand(String command) { this.command = command; }
}
