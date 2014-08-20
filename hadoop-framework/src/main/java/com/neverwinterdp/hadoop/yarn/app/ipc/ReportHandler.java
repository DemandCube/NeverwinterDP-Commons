package com.neverwinterdp.hadoop.yarn.app.ipc;

import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerInfo;

public interface ReportHandler {
  public void onReport(AppMaster appMaster, AppWorkerContainerInfo containerInfo, ReportData data) ;
}
