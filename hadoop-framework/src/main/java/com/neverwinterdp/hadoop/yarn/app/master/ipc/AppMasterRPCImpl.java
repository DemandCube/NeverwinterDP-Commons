package com.neverwinterdp.hadoop.yarn.app.master.ipc;

import java.net.InetSocketAddress;

import com.neverwinterdp.hadoop.yarn.app.master.AppMasterMonitor;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerInfo;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerProgressStatus;
import com.neverwinterdp.util.JSONSerializer;


public class AppMasterRPCImpl implements AppMasterRPC {
  private AppMasterMonitor appMonitor ;
  
  public AppMasterRPCImpl(AppMasterMonitor appMonitor) {
    this.appMonitor = appMonitor ;
  }
  
  public String ping(String msg) { return msg ; }

  public void   setRpcAddress(int containerId, String ip, int port) {
    InetSocketAddress addr = new InetSocketAddress(ip, port) ;
    AppWorkerContainerInfo containerInfo = appMonitor.getContainerInfo(containerId) ;
    containerInfo.setRpcAddress(addr);
  }
  
  public void progress(int containerId, AppWorkerContainerProgressStatus status) {
    AppWorkerContainerInfo containerInfo = appMonitor.getContainerInfo(containerId) ;
    containerInfo.setProgressStatus(status);
  }
  
  public String getAppMasterMonitorAsJSON() { return JSONSerializer.INSTANCE.toString(appMonitor); }
}
