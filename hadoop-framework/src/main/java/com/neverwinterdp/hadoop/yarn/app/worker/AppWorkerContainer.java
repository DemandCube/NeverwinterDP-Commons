package com.neverwinterdp.hadoop.yarn.app.worker;

import java.net.InetAddress;
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
import com.neverwinterdp.hadoop.yarn.app.AppConfig;
import com.neverwinterdp.hadoop.yarn.app.master.ipc.AppMasterRPC;
import com.neverwinterdp.hadoop.yarn.app.worker.ipc.AppWorkerContainerRPC;
import com.neverwinterdp.hadoop.yarn.app.worker.ipc.AppWorkerContainerRPCImpl;

public class AppWorkerContainer {
  static {
    System.setProperty("java.net.preferIPv4Stack", "true") ;
  }
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(AppWorkerContainer.class.getName());
  
  private AppConfig config ;
  private RPC.Server containerRPCServer ;
  private AppMasterRPC appMasterRPC ;
  private AppWorker worker ;
  private AppWorkerContainerProgressStatus progressStatus ;
  
  public AppWorkerContainer(AppConfig config) {
    this.config = config ;
    try {
      Configuration rpcConf = new Configuration() ;
      //RPC.setProtocolEngine(rpcConf, AppMasterRPC.class, ProtobufRpcEngine.class);
      //RPC.setProtocolEngine(rpcConf, AppWorkerContainerRPC.class, ProtobufRpcEngine.class);
      rpcConf.set(
          CommonConfigurationKeys.IO_SERIALIZATIONS_KEY, 
          JavaSerialization.class.getName() + "," + 
              WritableSerialization.class.getName() + "," +
              AvroSerialization.class.getName()
          ) ;
      containerRPCServer = 
          new RPC.Builder(rpcConf).
          setInstance(new AppWorkerContainerRPCImpl()).
          setProtocol(AppWorkerContainerRPC.class).
          setBindAddress(InetAddress.getLocalHost().getHostAddress()).
          build();
      containerRPCServer.start();

      InetSocketAddress rpcAddr = new InetSocketAddress(config.appHostName, config.appRpcPort) ;

      appMasterRPC = 
          RPC.getProxy(AppMasterRPC.class, RPC.getProtocolVersion(AppMasterRPC.class), rpcAddr, rpcConf);
      appMasterRPC.ping("hello") ;
      InetSocketAddress addr = containerRPCServer.getListenerAddress() ;
      appMasterRPC.setRpcAddress(config.getAppWorkerContainerId(), addr.getAddress().getHostAddress(), addr.getPort());

      Class<AppWorker> appWorkerClass = (Class<AppWorker>) Class.forName(config.worker) ;
      worker = appWorkerClass.newInstance() ;
    } catch(Throwable error) {
      LOGGER.error("Error" , error);
      onDestroy() ;
    }
  }
  
  public AppConfig getConfig() { return this.config ; }
  
  public AppMasterRPC getAppMasterRPC() { return this.appMasterRPC ; }

  public void reportProgress(float progress) {
    progressStatus.setProgress(progress);
    appMasterRPC.progress(config.getAppWorkerContainerId(), progressStatus);
  }
  
  public void run() {
    progressStatus = new AppWorkerContainerProgressStatus(AppWorkerContainerState.RUNNING) ;
    int workerContainerId = config.getAppWorkerContainerId() ;
    try {
      appMasterRPC.progress(workerContainerId, progressStatus) ;
      
      worker.run(this) ;
      
      progressStatus.setProgress(1);
      progressStatus.setContainerState(AppWorkerContainerState.FINISHED);
      appMasterRPC.progress(workerContainerId, progressStatus) ;
    } catch(Throwable error) {
      LOGGER.error("Error", error);
      progressStatus.setError(error);
      appMasterRPC.progress(workerContainerId, progressStatus);
    } finally {
      onDestroy() ;
    }
  }
  
  public void onDestroy() {
    LOGGER.info("Start onDestroy()");
    if(this.containerRPCServer != null) {
      this.containerRPCServer.stop() ; 
    }
    LOGGER.info("Finish onDestroy()");
  }
  
  public void onKill() {
    onDestroy() ;
    System.exit(0) ;
  }
  
  static public void main(String[] args) throws Exception {
    AppConfig options = new AppConfig() ;
    new JCommander(options, args) ;
    new AppWorkerContainer(options).run() ;
  }
}
