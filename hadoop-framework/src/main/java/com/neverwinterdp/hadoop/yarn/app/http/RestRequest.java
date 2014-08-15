package com.neverwinterdp.hadoop.yarn.app.http;


public class RestRequest {
  private String command ;
  
  public RestRequest() {} 
  
  public RestRequest(String command) {
    this.command = command ;
  }
  
  
  public String getCommand() { return command; }
  public void setCommand(String command) { this.command = command; }
}
