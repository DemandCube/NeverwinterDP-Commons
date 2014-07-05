package com.neverwinterdp.server.gateway.http;

import com.neverwinterdp.server.gateway.CommandParams;

public class HttpGatewayRequest {
  private String group ;
  private String command ;
  private CommandParams params = new CommandParams() ;
  
  public HttpGatewayRequest() {} 
  
  public HttpGatewayRequest(String group, String command) {
    this.group   = group ;
    this.command = command ;
  }
  
  public String getGroup() { return group; }
  public void setGroup(String group) { this.group = group; }
  
  public String getCommand() { return command; }
  public void setCommand(String command) { this.command = command; }
  
  public CommandParams getParams() { return params; }
  public void setParams(CommandParams params) { this.params = params ;}
}
