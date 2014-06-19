package com.neverwinterdp.hadoop.yarn.app.hello;

import java.io.IOException;

import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.hadoop.yarn.app.AppContainerConfig;
import com.neverwinterdp.hadoop.yarn.app.ContainerManager;
import com.neverwinterdp.hadoop.yarn.app.AppMaster;
import com.neverwinterdp.hadoop.yarn.app.AppMonitor;
import com.neverwinterdp.hadoop.yarn.app.ContainerInfo;
import com.neverwinterdp.util.text.TabularPrinter;

public class HelloAppContainerManger implements ContainerManager {
  protected static final Logger LOGGER = LoggerFactory.getLogger(HelloAppContainerManger.class);
  
  public void onInit(AppMaster appMaster) {
    //request for a bunch of container
    for (int i = 0; i < 2; i++) {
      ContainerRequest containerReq = 
          appMaster.createContainerRequest(0/*priority*/, 1/*core*/, 128/*memory*/);
      appMaster.asyncAdd(containerReq) ;
    }
  }

  public void onAllocatedContainer(AppMaster master, Container container) {
    try {
      AppContainerConfig config = new AppContainerConfig(master, container) ;
      config.setWorker(HelloWorker.class) ;
      master.startContainer(container, config.toCommand()) ;
      LOGGER.info("Start container with command: " + config.toCommand());
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
      Thread.sleep(10000);
    } catch (InterruptedException ex) {
    }
  }

  public void onShutdownRequest(AppMaster appMaster)  {
    
  }
  
  public void onExit(AppMaster appMaster) {
    LOGGER.info("Start onExit(AppMaster appMaster)");
    AppMonitor appMonitor = appMaster.getAppMonitor() ;
    ContainerInfo[] info = appMonitor.getContainerInfos() ;
    int[] colWidth = {20, 20, 20, 20} ;
    TabularPrinter printer = new TabularPrinter(System.out, colWidth) ;
    printer.header("Id", "Progress", "Error", "State");
    for(ContainerInfo sel : info) {
      printer.row(
        sel.getContainerId().getId(), 
        sel.getProgressStatus().getProgress(),
        sel.getProgressStatus().getError() != null,
        sel.getProgressStatus().getContainerState());
    }
    LOGGER.info("Finish onExit(AppMaster appMaster)");
  }
}