package com.neverwinterdp.hadoop.yarn.app.master.ipc;

import org.apache.hadoop.ipc.ProtocolInfo;

import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerProgressStatus;

@ProtocolInfo(protocolName = "AppMasterRPC", protocolVersion = 1)
public interface AppMasterRPC {
  public String ping(String msg) ;
  public void   setRpcAddress(int containerId, String ip, int port);
  public void   progress(int containerId, AppWorkerContainerProgressStatus status) ;
  
  public String getAppMasterMonitorAsJSON() ;
}