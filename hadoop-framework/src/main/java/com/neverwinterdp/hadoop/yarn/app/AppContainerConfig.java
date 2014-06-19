package com.neverwinterdp.hadoop.yarn.app;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.yarn.api.records.Container;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;

public class AppContainerConfig {
  @Parameter(names = "--container-id", required=true, description = "The allocated container id")
  public int containerId ;
  
  @Parameter(names = "--app-master-rpc-ip", required=true, description = "The app master contact ip address")
  public String appMasterRpcIpAddress ;
 
  @Parameter(names = "--app-master-rpc-port", required=true, description = "The app master listen port")
  public int appMasterRpcPort ;
 
  @Parameter(names = "--worker", required=true, description = "App worker")
  public String worker ;
  
  @DynamicParameter(names = "--conf:", description = "The yarn configuration overrided properties")
  public Map<String, String> conf = new HashMap<String, String>()  ;

  public AppContainerConfig() { }
  
  public AppContainerConfig(AppMaster appMaster, Container allocatedContainer) {
    this.containerId = allocatedContainer.getId().getId() ;
    appMaster.getRPCServer() ;
    InetSocketAddress addr = appMaster.getRPCServer().getListenerAddress() ;
    this.appMasterRpcIpAddress = addr.getAddress().getHostAddress() ;
    this.appMasterRpcPort = addr.getPort() ;
  }
  
  public void setWorker(Class<?> type) { worker = type.getName() ; }
  
  public String toCommand() {
    StringBuilder b = new StringBuilder() ;
    b.append("/usr/bin/java ").append(AppContainer.class.getName()) ;
    b.append(" --container-id ").append(this.containerId) ;
    b.append(" --app-master-rpc-ip ").append(this.appMasterRpcIpAddress) ;
    b.append(" --app-master-rpc-port ").append(this.appMasterRpcPort) ;
    b.append(" --worker ").append(this.worker) ;
    for(Map.Entry<String, String> entry : conf.entrySet()) {
      b.append(" --conf:").append(entry.getKey()).append("=").append(entry.getValue()) ;
    }
    return b.toString() ;
  } 
}