package com.neverwinterdp.hadoop.yarn.app;

import java.net.InetSocketAddress;


public class AppMasterRPCImpl implements AppMasterRPC {
  private AppMonitor appMonitor ;
  
  public AppMasterRPCImpl(AppMonitor appMonitor) {
    this.appMonitor = appMonitor ;
  }
  
  public String ping(String msg) { return msg ; }

  public void   setRpcAddress(int containerId, String ip, int port) {
    InetSocketAddress addr = new InetSocketAddress(ip, port) ;
    ContainerInfo containerInfo = appMonitor.getContainerInfo(containerId) ;
    containerInfo.setRpcAddress(addr);
  }
  
  public void progress(int containerId, ContainerProgressStatus status) {
    ContainerInfo containerInfo = appMonitor.getContainerInfo(containerId) ;
    containerInfo.setProgressStatus(status);
  }
}
