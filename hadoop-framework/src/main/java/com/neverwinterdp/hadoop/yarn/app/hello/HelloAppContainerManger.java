package com.neverwinterdp.hadoop.yarn.app.hello;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.HttpConfig.Policy;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.webapp.WebApp;
import org.apache.hadoop.yarn.webapp.WebApps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.hadoop.yarn.app.AppConfig;
import com.neverwinterdp.hadoop.yarn.app.hello.webapp.AMWebApp;
import com.neverwinterdp.hadoop.yarn.app.hello.webapp.AppContext;
import com.neverwinterdp.hadoop.yarn.app.hello.webapp.RunningAppContext;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.hadoop.yarn.app.master.AppMasterContainerManager;
import com.neverwinterdp.hadoop.yarn.app.master.AppMasterMonitor;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerInfo;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerState;
import com.neverwinterdp.util.text.TabularPrinter;

public class HelloAppContainerManger implements AppMasterContainerManager {
  protected static final Logger LOGGER = LoggerFactory.getLogger(HelloAppContainerManger.class);

  private String trackingURL ;
  private int    appRpcPort ;
  
  private WebApp webApp ;
  private AppContext appContext  ;
  
  public void onInit(AppMaster appMaster) {
    AppConfig config = appMaster.getConfig() ;
    config.setWorker(HelloWorker.class) ;
    
    Configuration conf = appMaster.getConfiguration() ;
    this.appContext = new RunningAppContext(config.appId) ;
    System.out.println("BEFORE CREATE WebApp!!!!!!!!!!") ;
    webApp =
        WebApps.$for("hello", AppContext.class, appContext, "ws")
          .withHttpPolicy(conf, Policy.HTTP_ONLY).start(new AMWebApp());
    InetSocketAddress listenAddr = NetUtils.getConnectAddress(webApp.getListenerAddress()) ;
    this.trackingURL = "http://" + listenAddr.getAddress().getHostAddress() + ":" + webApp.port() ;
    System.out.println("TRACKING URL = " + trackingURL) ;
    this.appRpcPort = config.appRpcPort ;
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
      AppConfig config = master.getConfig() ;
      master.startContainer(container) ;
      LOGGER.info("Start container with command: " + config.buildWorkerCommand());
    } catch (YarnException e) {
      LOGGER.error("Error on start a container", e);
    } catch (IOException e) {
      LOGGER.error("Error on start a container", e);
    }
  }

  public void onCompleteContainer(AppMaster master, ContainerStatus status, AppWorkerContainerInfo containerInfo) {
  }

  public void onFailedContainer(AppMaster master, ContainerStatus status, AppWorkerContainerInfo containerInfo) {
  }

  public void onShutdownRequest(AppMaster appMaster)  {
    
  }
  
  public void onExit(AppMaster appMaster) {
    LOGGER.info("Start onExit(AppMaster appMaster)");
    AppMasterMonitor appMonitor = appMaster.getAppMonitor() ;
    AppWorkerContainerInfo[] info = appMonitor.getContainerInfos() ;
    int[] colWidth = {20, 20, 20, 20} ;
    TabularPrinter printer = new TabularPrinter(System.out, colWidth) ;
    printer.header("Id", "Progress", "Error", "State");
    for(AppWorkerContainerInfo sel : info) {
      printer.row(
        sel.getContainerId(), 
        sel.getProgressStatus().getProgress(),
        sel.getProgressStatus().getError() != null,
        sel.getProgressStatus().getContainerState());
    }
    LOGGER.info("Finish onExit(AppMaster appMaster)");
  }
  
  public void waitForComplete(AppMaster appMaster) {
    LOGGER.info("Start waitForComplete(AppMaster appMaster)");
    AppConfig appConfig = appMaster.getConfig() ;
    try {
      boolean finished = false ;
      while(!finished) {
        synchronized(this) {
          this.wait(500);
        } 
        AppMasterMonitor monitor = appMaster.getAppMonitor() ;
        AppWorkerContainerInfo[] cinfos = monitor.getContainerInfos() ;
        if(cinfos.length < appConfig.appNumOfWorkers)  continue ;
        finished = true; 
        for(AppWorkerContainerInfo sel : cinfos) {
          if(!sel.getProgressStatus().getContainerState().equals(AppWorkerContainerState.FINISHED)) {
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

  @Override
  public String getTrackingURL() { return this.trackingURL ; }

  @Override
  public int getAppRPCPort() { return this.appRpcPort ; }
}