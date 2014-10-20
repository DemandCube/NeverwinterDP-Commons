package com.neverwinterdp.hadoop.yarn.sample;

import java.io.IOException;

import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.hadoop.yarn.app.AppConfig;
import com.neverwinterdp.hadoop.yarn.app.AppContainerInfoHolder;
import com.neverwinterdp.hadoop.yarn.app.AppInfo;
import com.neverwinterdp.hadoop.yarn.app.protocol.AppContainerStatus;
import com.neverwinterdp.hadoop.yarn.app.protocol.ProcessStatus;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.hadoop.yarn.app.master.AppMasterContainerManager;
import com.neverwinterdp.hadoop.yarn.app.master.AppMasterMonitor;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerInfo;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerState;
import com.neverwinterdp.util.text.TabularPrinter;

public class HelloAppContainerManger implements AppMasterContainerManager {
  protected static final Logger LOGGER = LoggerFactory.getLogger(HelloAppContainerManger.class);

  public void onInit(AppMaster appMaster) {
    AppConfig config = appMaster.getAppConfig() ;
    config.setWorkerByType(HelloWorker.class) ;
  }
  
  public void onRequestContainer(AppMaster appMaster) {
    //request for a bunch of container
    for (int i = 0; i < 2; i++) {
      ContainerRequest containerReq = 
          appMaster.createContainerRequest(0/*priority*/, 1/*core*/, 128/*memory*/);
      appMaster.asyncAdd(containerReq) ;
    }
  }

  public void onAllocatedContainer(AppMaster master, Container container) {
    try {
      AppConfig config = master.getAppConfig() ;
      master.startContainer(container) ;
      LOGGER.info("Start container with command: " + config.buildWorkerCommand());
    } catch (YarnException e) {
      LOGGER.error("Error on start a container", e);
    } catch (IOException e) {
      LOGGER.error("Error on start a container", e);
    }
  }

  public void onCompleteContainer(AppMaster master, AppContainerInfoHolder containerInfo, ContainerStatus status) {
  }

  public void onFailedContainer(AppMaster master, AppContainerInfoHolder containerInfo, ContainerStatus status) {
  }

  public void onShutdownRequest(AppMaster appMaster)  {
    
  }
  
  public void onExit(AppMaster appMaster) {
    LOGGER.info("Start onExit(AppMaster appMaster)");
    AppInfo appMonitor = appMaster.getAppInfo() ;
    AppContainerInfoHolder[] info = appMonitor.getAppContainerInfoHolders();
    int[] colWidth = {20, 20, 20, 20} ;
    TabularPrinter printer = new TabularPrinter(System.out, colWidth) ;
    printer.header("Id", "Progress", "Error", "State");
    for(AppContainerInfoHolder sel : info) {
      AppContainerStatus status = sel.getAppContainerStatus() ;
      printer.row(
        status.getContainerId(), 
        status.getProgress(),
        status.getErrorStacktrace() != null,
        status.getProcessStatus());
    }
    LOGGER.info("Finish onExit(AppMaster appMaster)");
  }
  
  public void waitForComplete(AppMaster appMaster) {
    LOGGER.info("Start waitForComplete(AppMaster appMaster)");
    AppConfig appConfig = appMaster.getAppConfig() ;
    try {
      boolean finished = false ;
      while(!finished) {
        synchronized(this) {
          this.wait(500);
        } 
        AppInfo monitor = appMaster.getAppInfo() ;
        AppContainerInfoHolder[] cinfos = monitor.getAppContainerInfoHolders() ;
        if(cinfos.length < appConfig.appNumOfWorkers)  continue ;
        finished = true; 
        for(AppContainerInfoHolder sel : cinfos) {
          ProcessStatus processStatus = sel.getAppContainerStatus().getProcessStatus() ;
          if(!processStatus.equals(ProcessStatus.TERMINATED)) {
            finished = false ;
            break ;
          }
        }
      }
    } catch (InterruptedException ex) {
      LOGGER.error("wait interruption: ", ex);
    }
    LOGGER.info("Finish waitForComplete(AppMaster appMaster)");
  }
}