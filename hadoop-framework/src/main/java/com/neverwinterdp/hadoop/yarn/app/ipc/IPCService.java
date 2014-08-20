package com.neverwinterdp.hadoop.yarn.app.ipc;

import org.apache.hadoop.ipc.ProtocolInfo;

import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerProgressStatus;

@ProtocolInfo(protocolName = "IPCService", protocolVersion = 1)
public interface IPCService {
  public String ping(String msg) ;
  public void   report(int containerId, AppWorkerContainerProgressStatus status) ;
  public void   report(String name, int containerId, ReportData data) ;
  
  public Response getMonitorData() ;
}