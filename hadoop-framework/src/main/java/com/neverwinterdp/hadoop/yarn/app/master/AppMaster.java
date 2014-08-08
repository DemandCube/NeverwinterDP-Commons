package com.neverwinterdp.hadoop.yarn.app.master;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.io.serializer.JavaSerialization;
import org.apache.hadoop.io.serializer.WritableSerialization;
import org.apache.hadoop.io.serializer.avro.AvroSerialization;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerExitStatus;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.NMClient;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.Records;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.neverwinterdp.hadoop.yarn.app.AppConfig;
import com.neverwinterdp.hadoop.yarn.app.Util;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerInfo;

public class AppMaster {
  static {
    System.setProperty("java.net.preferIPv4Stack", "true") ;
  }
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(AppMaster.class.getName());
  
  private AppConfig config ;
  private RPC.Server rpcServer ;
  private AMRMClient<ContainerRequest> amrmClient ;
  private AMRMClientAsync<ContainerRequest> amrmClientAsync ;
  private NMClient nmClient;
  private Configuration conf;
  
  private AppMasterMonitor appMonitor = new AppMasterMonitor() ;
  private AppMasterContainerManager containerManager ;
  
  public AppMaster() {
  }
  
  public AppConfig getConfig() { return this.config ; }
  
  public Configuration getConfiguration() { return this.conf ; }
  
  public AppMasterMonitor getAppMonitor() { return this.appMonitor ; }

  public AMRMClient<ContainerRequest> getAMRMClient() { return this.amrmClient ; }
  
  public NMClient getNMClient() { return this.nmClient ; }
  
  public RPC.Server getRPCServer()  { return this.rpcServer ; }
  
  public boolean run(String[] args) throws Exception {
    this.config = new AppConfig() ;
    new JCommander(config, args) ;
    
    conf = new YarnConfiguration() ;
    config.overrideConfiguration(conf);

    Configuration rpcConf = new Configuration() ;
    rpcConf.set(
      CommonConfigurationKeys.IO_SERIALIZATIONS_KEY, 
      JavaSerialization.class.getName() + "," + 
      WritableSerialization.class.getName() + "," +
      AvroSerialization.class.getName()
    );
    //RPC.setProtocolEngine(rpcConf, AppMasterRPC.class, ProtobufRpcEngine.class);
    //RPC.setProtocolEngine(rpcConf, AppWorkerContainerRPC.class, ProtobufRpcEngine.class);
    rpcServer = 
        new RPC.Builder(rpcConf).
        setInstance(new AppMasterRPCImpl(appMonitor)).
        setProtocol(AppMasterRPC.class).
        setBindAddress(InetAddress.getLocalHost().getHostAddress()).
        setPort(config.appRpcPort).
        build();
    rpcServer.start() ;
    this.config.appHostName = rpcServer.getListenerAddress().getAddress().getHostAddress() ;
    this.config.appRpcPort = rpcServer.getListenerAddress().getPort() ;
    
    Class<?> containerClass = Class.forName(config.appContainerManager) ;
    containerManager = (AppMasterContainerManager)containerClass.newInstance() ;
    
    amrmClient = AMRMClient.createAMRMClient();
    amrmClientAsync = AMRMClientAsync.createAMRMClientAsync(amrmClient, 1000, new AMRMCallbackHandler());
    amrmClientAsync.init(conf);
    amrmClientAsync.start();

    nmClient = NMClient.createNMClient();
    nmClient.init(conf);
    nmClient.start();

    containerManager.onInit(this);
    // Register with RM
    RegisterApplicationMasterResponse registerResponse = 
      amrmClientAsync.registerApplicationMaster(config.appHostName, config.appRpcPort, containerManager.getTrackingURL());
    containerManager.onRequestContainer(this);
    containerManager.waitForComplete(this);
    containerManager.onExit(this);
    amrmClientAsync.unregisterApplicationMaster(FinalApplicationStatus.SUCCEEDED, "", "");
    amrmClientAsync.stop();
    amrmClientAsync.close(); 
    nmClient.stop();
    nmClient.close();
    this.rpcServer.stop() ; 
    return true;
  }

  public ContainerRequest createContainerRequest(int priority, int numOfCores, int memory) {
    //Priority for worker containers - priorities are intra-application
    Priority containerPriority = Priority.newInstance(priority);
    // Resource requirements for worker containers
    Resource resource = Resource.newInstance(memory, numOfCores);
    ContainerRequest containerReq = 
      new ContainerRequest(resource, null /* hosts*/, null /*racks*/, containerPriority);
    return containerReq;
  }
  
  public Container requestContainer(int priority, int numOfCores, int memory, long maxWait) throws YarnException, IOException, InterruptedException {
    ContainerRequest containerReq = createContainerRequest(priority, numOfCores, memory) ;
    amrmClient.addContainerRequest(containerReq);
    long stopTime = System.currentTimeMillis() + maxWait ;
    while (System.currentTimeMillis() < stopTime) {
      AllocateResponse response = amrmClient.allocate(0);
      List<Container> containers = response.getAllocatedContainers() ;
      if(containers.size() > 0)return containers.get(0) ;
      Thread.sleep(500);
    }
    return null;
  }
  
  public void asyncAdd(ContainerRequest containerReq) {
    amrmClientAsync.addContainerRequest(containerReq);
    appMonitor.onContainerRequest(containerReq);
  }
  
  public void add(ContainerRequest containerReq) {
    amrmClient.addContainerRequest(containerReq);
    appMonitor.onContainerRequest(containerReq);
  }
  
  public List<Container> getAllocatedContainers() throws YarnException, IOException {
    AllocateResponse response = amrmClient.allocate(0);
    return response.getAllocatedContainers() ;
  }
  
  public void startContainer(Container container) throws YarnException, IOException {
    ContainerLaunchContext ctx = Records.newRecord(ContainerLaunchContext.class);
    System.out.println("Setup the classpath for the container " + container.getId()) ;
    Map<String, String> appMasterEnv = new HashMap<String, String>();
    Util.setupAppMasterEnv(true, conf, appMasterEnv);
    ctx.setEnvironment(appMasterEnv);
    
    config.setAppWorkerContainerId(container.getId().getId());
    StringBuilder sb = new StringBuilder();
    List<String> commands = Collections.singletonList(
        sb.append(config.buildWorkerCommand()).
        append(" 1> ").append(ApplicationConstants.LOG_DIR_EXPANSION_VAR).append("/stdout").
        append(" 2> ").append(ApplicationConstants.LOG_DIR_EXPANSION_VAR).append("/stderr")
        .toString()
        );
    ctx.setCommands(commands);
    nmClient.startContainer(container, ctx);
    appMonitor.onAllocatedContainer(container, commands);
  }
  
  class AMRMCallbackHandler implements AMRMClientAsync.CallbackHandler {
    
    public void onContainersCompleted(List<ContainerStatus> statuses) {
      for (ContainerStatus status: statuses) {
        assert (status.getState() == ContainerState.COMPLETE);
        int exitStatus = status.getExitStatus();
        AppWorkerContainerInfo containerInfo = appMonitor.getContainerInfo(status.getContainerId().getId()) ;
        if (exitStatus != ContainerExitStatus.SUCCESS) {
          appMonitor.onFailedContainer(status);
          containerManager.onFailedContainer(AppMaster.this, status, containerInfo);
        } else {
          appMonitor.onCompletedContainer(status);
          containerManager.onCompleteContainer(AppMaster.this, status, containerInfo);
        }
      }
    }

    public void onContainersAllocated(List<Container> containers) {
      for (int i = 0; i < containers.size(); i++) {
        Container container = containers.get(i) ;
        containerManager.onAllocatedContainer(AppMaster.this, container);
      }
    }


    public void onNodesUpdated(List<NodeReport> updated) {
    }

    public void onError(Throwable e) {
      amrmClientAsync.stop();
    }

    public void onShutdownRequest() { 
      containerManager.onShutdownRequest(AppMaster.this); 
    }

    public float getProgress() { return 0; }
  }

  static public void main(String[] args) throws Exception {
    new AppMaster().run(args) ;
  }
}