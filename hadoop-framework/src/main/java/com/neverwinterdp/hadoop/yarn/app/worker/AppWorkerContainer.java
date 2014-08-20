package com.neverwinterdp.hadoop.yarn.app.worker;

import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.io.serializer.JavaSerialization;
import org.apache.hadoop.io.serializer.WritableSerialization;
import org.apache.hadoop.io.serializer.avro.AvroSerialization;
import org.apache.hadoop.ipc.RPC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.neverwinterdp.hadoop.yarn.app.AppInfo;
import com.neverwinterdp.hadoop.yarn.app.ipc.IPCService;

public class AppWorkerContainer {
  static {
    System.setProperty("java.net.preferIPv4Stack", "true") ;
  }
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(AppWorkerContainer.class.getName());
  
  private AppInfo config ;
  private IPCService ipcService ;
  private AppWorker worker ;
  private AppWorkerContainerProgressStatus progressStatus ;
  
  public AppWorkerContainer(AppInfo config) {
    this.config = config ;
    try {
      Configuration rpcConf = new Configuration() ;
      rpcConf.set(
          CommonConfigurationKeys.IO_SERIALIZATIONS_KEY, 
          JavaSerialization.class.getName() + "," + 
              WritableSerialization.class.getName() + "," +
              AvroSerialization.class.getName()
          ) ;
      InetSocketAddress rpcAddr = new InetSocketAddress(config.appHostName, config.appRpcPort) ;
      ipcService = 
          RPC.getProxy(IPCService.class, RPC.getProtocolVersion(IPCService.class), rpcAddr, rpcConf);
      ipcService.ping("hello") ;

      Class<AppWorker> appWorkerClass = (Class<AppWorker>) Class.forName(config.worker) ;
      worker = appWorkerClass.newInstance() ;
    } catch(Throwable error) {
      LOGGER.error("Error" , error);
      onDestroy() ;
    }
  }
  
  public AppInfo getConfig() { return this.config ; }
  
  public IPCService getAppMasterRPC() { return this.ipcService ; }

  public void reportProgress(float progress) {
    progressStatus.setProgress(progress);
    ipcService.report(config.getAppWorkerContainerId(), progressStatus);
  }
  
  public void run() {
    progressStatus = new AppWorkerContainerProgressStatus(AppWorkerContainerState.RUNNING) ;
    int workerContainerId = config.getAppWorkerContainerId() ;
    try {
      ipcService.report(workerContainerId, progressStatus) ;
      
      worker.run(this) ;
      
      progressStatus.setProgress(1);
      progressStatus.setContainerState(AppWorkerContainerState.FINISHED);
      ipcService.report(workerContainerId, progressStatus) ;
    } catch(Throwable error) {
      LOGGER.error("Error", error);
      progressStatus.setThrowableError(error);
      ipcService.report(workerContainerId, progressStatus);
    } finally {
      onDestroy() ;
    }
  }
  
  public void onDestroy() {
    LOGGER.info("Start  onDestroy()");
    LOGGER.info("Finish onDestroy()");
  }
  
  public void onKill() {
    onDestroy() ;
    System.exit(0) ;
  }
  
  static public void main(String[] args) throws Exception {
    AppInfo options = new AppInfo() ;
    new JCommander(options, args) ;
    new AppWorkerContainer(options).run() ;
  }
}
