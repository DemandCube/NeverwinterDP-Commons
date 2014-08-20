package com.neverwinterdp.hadoop.yarn.app.history;

public class AppHistorySendAck {
  private boolean success ;
  private String  info    ;
  
  public AppHistorySendAck() {} 
  
  public AppHistorySendAck(boolean success) {
    this.success = success ;
  }
  
  public boolean isSuccess() { return success; }
  public void setSuccess(boolean success) { this.success = success; }
  
  public String getInfo() { return info; }
  public void setInfo(String info) { this.info = info; }
}
