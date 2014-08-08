package com.neverwinterdp.hadoop.yarn.app.hello.webapp;

public class RunningAppContext implements AppContext {
  private String applicationId ;
  
  public RunningAppContext(String applicationId) {
    this.applicationId = applicationId ;
  }
  
  public String getApplicationId() { return this.applicationId ; }
  
  public String getApplicationName() { return "Hello"; }

  public String getUser() { return "AUser"; }

}
