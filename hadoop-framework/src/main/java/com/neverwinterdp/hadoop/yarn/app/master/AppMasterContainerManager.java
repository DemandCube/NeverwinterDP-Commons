package com.neverwinterdp.hadoop.yarn.app.master;

import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;

import com.neverwinterdp.hadoop.yarn.app.AppContainerInfoHolder;

public interface AppMasterContainerManager {
  public void onInit(AppMaster appMaster) ;
  public void onRequestContainer(AppMaster appMaster) ;
  public void onAllocatedContainer(AppMaster master, Container container) ;
  public void onCompleteContainer(AppMaster master, AppContainerInfoHolder containerInfo, ContainerStatus status) ;
  public void onFailedContainer(AppMaster master, AppContainerInfoHolder containerInfo, ContainerStatus status) ;
  public void onShutdownRequest(AppMaster appMaster) ;
  public void onExit(AppMaster appMaster) ;
  
  public void waitForComplete(AppMaster appMaster);
}