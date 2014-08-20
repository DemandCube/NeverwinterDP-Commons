package com.neverwinterdp.hadoop.yarn.app.master;

import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;

import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerInfo;

public interface AppMasterContainerManager {
  public void onInit(AppMaster appMaster) ;
  public void onRequestContainer(AppMaster appMaster) ;
  public void onAllocatedContainer(AppMaster master, Container container) ;
  public void onCompleteContainer(AppMaster master, ContainerStatus status, AppWorkerContainerInfo containerInfo) ;
  public void onFailedContainer(AppMaster master, ContainerStatus status, AppWorkerContainerInfo containerInfo) ;
  public void onShutdownRequest(AppMaster appMaster) ;
  public void onExit(AppMaster appMaster) ;
  
  public void waitForComplete(AppMaster appMaster);
}