package com.neverwinterdp.hadoop.yarn.app.history;

import com.neverwinterdp.hadoop.yarn.app.AppInfo;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.hadoop.yarn.app.master.AppMasterMonitor;

public class AppHistory {
  private AppInfo appInfo ;
  private AppMasterMonitor appMonitor ;
  
  public AppHistory() { }
  
  public AppHistory(AppMaster appMaster) {
    this.appInfo = appMaster.getAppInfo() ;
    this.appMonitor = appMaster.getAppMonitor() ;
  }
  
  public AppInfo getAppInfo() { return appInfo; }
  public void setAppInfo(AppInfo appInfo) { this.appInfo = appInfo; }
  
  public AppMasterMonitor getAppMonitor() { return appMonitor; }
  public void setAppMonitor(AppMasterMonitor appMonitor) { this.appMonitor = appMonitor; }
}
