package com.neverwinterdp.hadoop.yarn.app.history;

public class AppHistoryDescription {
  private String appId ;
  private String startTime ;
  private String finishTime ;
  private String state ;
  
  public String getAppId() { return appId; }
  public void setAppId(String appId) { this.appId = appId; }
  
  public String getStartTime() { return startTime; }
  public void setStartTime(String startTime) { this.startTime = startTime; }
  
  public String getFinishTime() { return finishTime; }
  public void setFinishTime(String finishTime) { this.finishTime = finishTime; }
  
  public String getState() { return state; }
  public void setState(String state) { this.state = state; }
}
