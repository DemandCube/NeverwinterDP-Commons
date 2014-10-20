package com.neverwinterdp.hadoop.yarn.app.ipc;

import com.neverwinterdp.hadoop.yarn.app.protocol.AppContainerInfo;
import com.neverwinterdp.hadoop.yarn.app.protocol.AppContainerReport;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;

public interface ReportHandler {
  public void onReport(AppMaster appMaster, AppContainerInfo containerInfo, AppContainerReport report) ;
}
