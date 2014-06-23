package com.neverwinterdp.hadoop.yarn.app;

import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;

public interface ContainerManager {
  public void onInit(AppMaster appMaster) ;
  public void onAllocatedContainer(AppMaster master, Container container) ;
  public void onCompleteContainer(AppMaster master, ContainerStatus status, ContainerInfo containerInfo) ;
  public void onFailedContainer(AppMaster master, ContainerStatus status, ContainerInfo containerInfo) ;
  public void waitForComplete(AppMaster appMaster);
  public void onShutdownRequest(AppMaster appMaster) ;
  public void onExit(AppMaster appMaster) ;
}