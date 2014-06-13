package com.neverwinterdp.hadoop.yarn.hello;

import java.io.IOException;

import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.hadoop.yarn.AppContainerManager;
import com.neverwinterdp.hadoop.yarn.AppMaster;
import com.neverwinterdp.hadoop.yarn.AppMonitor;
import com.neverwinterdp.hadoop.yarn.ContainerInfo;

public class HelloAppContainerManger implements AppContainerManager {
  protected static final Logger LOGGER = LoggerFactory.getLogger(HelloAppContainerManger.class);
  
  public void onInit(AppMaster appMaster) {
    //request for a bunch of container
    for (int i = 0; i < 2; i++) {
      ContainerRequest containerReq = 
          appMaster.createContainerRequest(0/*priority*/, 1/*core*/, 128/*memory*/);
      appMaster.add(containerReq) ;
    }
  }

  public void onAllocatedContainer(AppMaster master, Container container) {
    try {
      master.startContainer(container, "date") ;
    } catch (YarnException e) {
      LOGGER.error("Error on start a container", e);
    } catch (IOException e) {
      LOGGER.error("Error on start a container", e);
    }
  }

  public void onCompleteContainer(AppMaster master, ContainerStatus status, ContainerInfo containerInfo) {
  }

  public void onFailedContainer(AppMaster master, ContainerStatus status, ContainerInfo containerInfo) {
  }

  public void waitForComplete(AppMaster appMaster) {
    try {
      Thread.sleep(5000);
    } catch (InterruptedException ex) {
    }
  }

  public void onExit(AppMaster appMaster) {
    LOGGER.info("Start onExit(AppMaster appMaster)");
    AppMonitor appMonitor = appMaster.getAppMonitor() ;
    ContainerInfo[] info = appMonitor.getContainerInfos() ;
    for(ContainerInfo sel : info) {
      if(!"SUCCESS".equals(sel.getCompleteStatus())) {
        LOGGER.error("failed on container with command " + sel.getCommands());
      }
    }
    LOGGER.info("Finish onExit(AppMaster appMaster)");
  }
}