package com.neverwinterdp.hadoop.yarn.app;

import com.neverwinterdp.hadoop.yarn.app.protocol.AppContainerInfo;
import com.neverwinterdp.hadoop.yarn.app.protocol.AppContainerReport;
import com.neverwinterdp.hadoop.yarn.app.protocol.AppContainerStatus;
import com.neverwinterdp.hadoop.yarn.app.protocol.ProcessStatus;

public class AppContainerInfoHolder {
  private AppContainerInfo.Builder info ;
  
  public AppContainerInfoHolder(int containerId) {
    info = AppContainerInfo.newBuilder() ;
    info.getStatusBuilder().setContainerId(containerId) ;
    info.getStatusBuilder().setStartTime(System.currentTimeMillis()) ;
  }
  
  public AppContainerInfo getAppContainerInfo() { return info.build() ; }

  public void setProcessStatus(ProcessStatus status) {
    info.getStatusBuilder().setProcessStatus(status) ;
  }
  
  public ProcessStatus getProgressStatus() { return info.getStatusBuilder().getProcessStatus() ; }
  
  public AppContainerStatus getAppContainerStatus() { return info.getStatus() ; }
  
  public AppContainerStatus.Builder getAppContainerStatusBuilder() { return info.getStatusBuilder() ; }
  
  public void setAppContainerStatus(AppContainerStatus status) {
    info.getStatusBuilder().mergeFrom(status) ;
  }
  
  public void setProgress(double progress) {
    info.getStatusBuilder().setProgress(progress) ;
  }
  
  public void mergeAppContainerReport(AppContainerReport report) {
    for(int i = 0; i < info.getReportsCount(); i++) {
      AppContainerReport.Builder sel = info.getReportsBuilder(i) ;
      if(sel.getName().equals(report.getName())) {
        sel.mergeFrom(report) ;
        return ;
      }
    }
    info.addReports(report);
  }
}
