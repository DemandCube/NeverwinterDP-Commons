package com.neverwinterdp.hadoop.yarn.app.history;

import com.neverwinterdp.hadoop.yarn.app.AppConfig;
import com.neverwinterdp.hadoop.yarn.app.AppInfo;
import com.neverwinterdp.hadoop.yarn.app.protocol.AppContainerInfo;
import com.neverwinterdp.hadoop.yarn.app.protocol.AppMasterInfo;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;

public class AppHistory {
  private AppConfig appConfig ;
  private AppMasterInfo appMasterInfo ;
  private AppContainerInfo[] containerInfo ;
  
  public AppHistory() { }
  
  public AppHistory(AppConfig appConfig, AppInfo appInfo) { 
    this.appConfig = appConfig ;
    this.appMasterInfo = appInfo.getAppMasterInfoHolder().getAppMasterInfo() ;
    this.containerInfo = appInfo.getAppContainerInfos() ;
  }
  
  public AppHistory(AppMaster appMaster) {
    this(appMaster.getAppConfig(), appMaster.getAppInfo()) ;
  }
  
  public AppConfig getAppConfig() { return appConfig; }
  public void setAppConfig(AppConfig appConfig) { this.appConfig = appConfig; }

  public AppMasterInfo getAppMasterInfo() { return appMasterInfo; }
  public void setAppMasterInfo(AppMasterInfo appMasterInfo) { this.appMasterInfo = appMasterInfo; }

  public AppContainerInfo[] getContainerInfo() { return containerInfo; }
  public void setContainerInfo(AppContainerInfo[] containerInfo) {
    this.containerInfo = containerInfo;
  }
}
