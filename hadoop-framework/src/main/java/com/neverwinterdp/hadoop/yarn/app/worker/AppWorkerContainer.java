package com.neverwinterdp.hadoop.yarn.app.worker;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.io.serializer.JavaSerialization;
import org.apache.hadoop.io.serializer.WritableSerialization;
import org.apache.hadoop.io.serializer.avro.AvroSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.google.protobuf.ServiceException;
import com.neverwinterdp.hadoop.yarn.app.AppConfig;
import com.neverwinterdp.hadoop.yarn.app.AppContainerInfoHolder;
import com.neverwinterdp.hadoop.yarn.app.protocol.IPCService;
import com.neverwinterdp.hadoop.yarn.app.protocol.ProcessStatus;
import com.neverwinterdp.netty.rpc.client.DefaultClientRPCController;
import com.neverwinterdp.netty.rpc.client.RPCClient;
import com.neverwinterdp.util.ExceptionUtil;

public class AppWorkerContainer {
  static {
    System.setProperty("java.net.preferIPv4Stack", "true") ;
  }
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(AppWorkerContainer.class.getName());
  
  private AppConfig config ;
  private RPCClient rpcClient ;
  private IPCService.BlockingInterface ipcService ;
  private AppWorker worker ;
  private AppContainerInfoHolder appContainerInfoHolder ;
  
  public AppWorkerContainer(AppConfig config) {
    this.config = config ;
    this.appContainerInfoHolder = new AppContainerInfoHolder(config.getAppWorkerContainerId()) ;
    try {
      Configuration rpcConf = new Configuration() ;
      rpcConf.set(
          CommonConfigurationKeys.IO_SERIALIZATIONS_KEY, 
          JavaSerialization.class.getName() + "," + 
              WritableSerialization.class.getName() + "," +
              AvroSerialization.class.getName()
      ) ;
      
      rpcClient = new RPCClient(config.appHostName, config.appRpcPort) ;
      ipcService = IPCService.newBlockingStub(rpcClient.getRPCChannel()) ;
      
      Class<AppWorker> appWorkerClass = (Class<AppWorker>) Class.forName(config.worker) ;
      worker = appWorkerClass.newInstance() ;
    } catch(Throwable error) {
      LOGGER.error("Error" , error);
      onDestroy() ;
    }
  }
  
  public AppConfig getConfig() { return this.config ; }
  
  public IPCService.BlockingInterface getAppMasterRPC() { return this.ipcService ; }

  public void reportStatus() {
    try {
      ipcService.updateAppContainerStatus(new DefaultClientRPCController(), appContainerInfoHolder.getAppContainerStatus());
    } catch (ServiceException e) {
      LOGGER.error("Cannot report progress", e);
    }
  }
  
  public void reportProgress(double progress) {
    appContainerInfoHolder.setProgress(progress);
    reportStatus() ;
  }
  
  public void run() {
    appContainerInfoHolder.setProcessStatus(ProcessStatus.RUNNING);
    appContainerInfoHolder.setProgress(0d);
    reportStatus() ;
    try {
      worker.run(this) ;
      appContainerInfoHolder.setProgress(1d);
      reportStatus() ;
    } catch(Throwable error) {
      LOGGER.error("Error", error);
      appContainerInfoHolder.getAppContainerStatusBuilder().setErrorStacktrace(ExceptionUtil.getStackTrace(error)) ;
      reportStatus() ;
    } finally {
      appContainerInfoHolder.setProcessStatus(ProcessStatus.DESTROY);
      reportStatus() ;
      onDestroy() ;
      appContainerInfoHolder.setProcessStatus(ProcessStatus.TERMINATED);
      reportStatus() ;
      if(rpcClient != null) rpcClient.close() ; 
    }
  }
  
  public void onDestroy() {
    LOGGER.info("Start  onDestroy()");
    LOGGER.info("Finish onDestroy()");
  }
  
  public void onKill() {
    onDestroy() ;
    if(rpcClient != null) rpcClient.close() ; 
    System.exit(0) ;
  }
  
  static public void main(String[] args) throws Exception {
    AppConfig options = new AppConfig() ;
    new JCommander(options, args) ;
    new AppWorkerContainer(options).run() ;
  }
}
