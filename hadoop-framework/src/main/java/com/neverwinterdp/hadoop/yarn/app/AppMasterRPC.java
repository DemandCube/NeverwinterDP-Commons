package com.neverwinterdp.hadoop.yarn.app;

import org.apache.hadoop.ipc.ProtocolInfo;

@ProtocolInfo(protocolName = "AppMasterRPC", protocolVersion = 1)
public interface AppMasterRPC {
  public String ping(String msg) ;
  public void   setRpcAddress(int containerId, String ip, int port);
  public void   progress(int containerId, ContainerProgressStatus status) ;
}
