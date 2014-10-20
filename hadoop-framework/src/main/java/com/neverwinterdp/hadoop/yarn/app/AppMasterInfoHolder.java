package com.neverwinterdp.hadoop.yarn.app;

import com.neverwinterdp.hadoop.yarn.app.protocol.AppMasterInfo;
import com.neverwinterdp.hadoop.yarn.app.protocol.ProcessStatus;

public class AppMasterInfoHolder {
  private AppMasterInfo.Builder info ;
  
  public AppMasterInfoHolder() {
    info = AppMasterInfo.newBuilder();
    info.getStatusBuilder().setStartTime(System.currentTimeMillis());
    info.getStatusBuilder().setProcessStatus(ProcessStatus.INIT);
  }
  
  public AppMasterInfo getAppMasterInfo() { return info.build() ; }
}
