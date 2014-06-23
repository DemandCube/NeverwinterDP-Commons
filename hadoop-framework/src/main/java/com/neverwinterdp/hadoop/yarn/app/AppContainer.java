package com.neverwinterdp.hadoop.yarn.app;

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

public class AppContainer {
  static {
    System.setProperty("java.net.preferIPv4Stack", "true") ;
  }
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(AppContainer.class.getName());
  
  private AppContainerConfig config ;
  private RPC.Server containerRPCServer ;
  private AppMasterRPC appMasterRPC ;
  private AppWorker worker ;
  private ContainerProgressStatus progressStatus ;
  
  public AppContainer(AppContainerConfig config) {
    this.config = config ;
    try {
      Configuration rpcConf = new Configuration() ;
      //RPC.setProtocolEngine(rpcConf, AppMasterRPC.class, ProtobufRpcEngine.class);
      //RPC.setProtocolEngine(rpcConf, AppContainerRPC.class, ProtobufRpcEngine.class);
      rpcConf.set(
          CommonConfigurationKeys.IO_SERIALIZATIONS_KEY, 
          JavaSerialization.class.getName() + "," + 
              WritableSerialization.class.getName() + "," +
              AvroSerialization.class.getName()
          ) ;
      containerRPCServer = 
          new RPC.Builder(rpcConf).
          setInstance(new AppContainerRPCImpl()).
          setProtocol(AppContainerRPC.class).
          setBindAddress(InetAddress.getLocalHost().getHostAddress()).
          build();
      containerRPCServer.start();

      InetSocketAddress rpcAddr = new InetSocketAddress(config.appMasterRpcIpAddress, config.appMasterRpcPort) ;

      appMasterRPC = 
          RPC.getProxy(AppMasterRPC.class, RPC.getProtocolVersion(AppMasterRPC.class), rpcAddr, rpcConf);
      appMasterRPC.ping("hello") ;
      InetSocketAddress addr = containerRPCServer.getListenerAddress() ;
      appMasterRPC.setRpcAddress(config.containerId, addr.getAddress().getHostAddress(), addr.getPort());

      Class<AppWorker> appWorkerClass = (Class<AppWorker>) Class.forName(config.worker) ;
      worker = appWorkerClass.newInstance() ;
    } catch(Throwable error) {
      LOGGER.error("Error" , error);
      onDestroy() ;
    }
  }
  
  public AppContainerConfig getConfig() { return this.config ; }
  
  public AppMasterRPC getAppMasterRPC() { return this.appMasterRPC ; }

  public void reportProgress(float progress) {
    progressStatus.setProgress(progress);
    appMasterRPC.progress(config.containerId, progressStatus);
  }
  
  public void run() {
    progressStatus = new ContainerProgressStatus(ContainerState.RUNNING) ;
    try {
      appMasterRPC.progress(config.containerId, progressStatus) ;
      
      worker.run(this) ;
      
      progressStatus.setProgress(1);
      progressStatus.setContainerState(ContainerState.FINISHED);
      appMasterRPC.progress(config.containerId, progressStatus) ;
    } catch(Throwable error) {
      LOGGER.error("Error", error);
      progressStatus.setError(error);
      appMasterRPC.progress(config.containerId, progressStatus);
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
    AppContainerConfig options = new AppContainerConfig() ;
    new JCommander(options, args) ;
    new AppContainer(options).run() ;
  }
}
