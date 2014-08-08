package com.neverwinterdp.hadoop.yarn.app;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainer;

public class AppConfig {
  @Parameter(names = "--mini-cluster-env", description = "Setup the mini cluster env for testing")
  public boolean miniClusterEnv = false ;
  
  @Parameter(names = "--app-id", description = "The application id")
  public String appId ;
  
  @Parameter(names = "--app-home-local", description = "The shared location of the application directory")
  public String appHomeLocal ;
  
  @Parameter(
    names = "--app-home", 
    description = "The local app home on the client, that should be copy and deploy to app home share"
  )
  public String appHome ;
  
  @Parameter(names = "--app-name", description = "The application name")
  public String appName = "Yarn Application"  ;
  
  @Parameter(names = "--app-host-name", description = "The application host name")
  public String appHostName  ;
  
  @Parameter(names = "--app-rpc-port", description = "The application rpc port")
  public int appRpcPort = 6500  ;
  
  @Parameter(names = "--app-tracking-url", description = "The application tracking url")
  public String appTrackingUrl  ;
  
  
  @Parameter(names = "--app-container-manager", description = "The application container manager class")
  public String appContainerManager   ;
  
  @Parameter(names = "--app-max-memory", description = "Maximum amount of memory allocate to jvm")
  public int appMaxMemory = 128 ;
  
  @Parameter(names = "--app-num-of-worker", description = "Number of worker")
  public int appNumOfWorkers = 1;
  
  @Parameter(names = "--worker-class", description = "App worker")
  public String worker ;
  
  @Parameter(names = "--worker-max-memory", description = "Maximum amount of memory allocate to jvm")
  public int workerMaxMemory = 128 ;
 
  @Parameter(names = "--worker-num-of-core", description = "Maximum amount of memory allocate to jvm")
  public int workerNumOfCore = 1 ;
 
  @DynamicParameter(names = "--conf:", description = "The yarn configuration overrided properties")
  public Map<String, String> yarnConf = new HashMap<String, String>()  ;
  
  public int getAppWorkerContainerId() {
    String val  = yarnConf.get("worker-container-id") ;
    if(val == null) return 0;
    return Integer.parseInt(val) ;
  }
  
  public void setAppWorkerContainerId(int id) {
    yarnConf.put("worker-container-id", Integer.toString(id)) ;
  }

  public void setWorker(Class<?> type) { this.worker = type.getName(); }
  
  public String buildMasterCommand() {
    StringBuilder b = new StringBuilder() ;
    b.append("java ").append(" -Xmx" + appMaxMemory + "m ").
      append(AppMaster.class.getName()) ;
    addParameters(b);
    System.out.println("Master Command: " + b.toString());
    return b.toString() ;
  }
  
  public String buildWorkerCommand() {
    StringBuilder b = new StringBuilder() ;
    b.append("java ").append(" -Xmx" + workerMaxMemory + "m ").append(AppWorkerContainer.class.getName()) ;
    addParameters(b);
    return b.toString() ;
  } 
  
  public void overrideConfiguration(Configuration aconf) {
    for(Map.Entry<String, String> entry : yarnConf.entrySet()) {
      aconf.set(entry.getKey(), entry.getValue()) ;
    }
  }
  
  private void addParameters(StringBuilder b) {
    if(this.miniClusterEnv) {
      b.append(" --mini-cluster-env ") ;
    }
    
    if(appId != null) {
      b.append(" --app-id ").append(this.appId) ;
    }
    
    if(appHomeLocal != null) {
      b.append(" --app-home ").append(this.appHome) ;
    }
    
    if(appHome != null) {
      b.append(" --app-home-local ").append(this.appHomeLocal) ;
    }
    
    if(appName != null) {
      b.append(" --app-name ").append(this.appName) ;
    }
    
    if(appHostName != null) {
      b.append(" --app-host-name ").append(this.appHostName) ;
    }
    b.append(" --app-rpc-port ").append(this.appRpcPort) ;
    if(appTrackingUrl != null) {
      b.append(" --app-tracking-url ").append(this.appTrackingUrl) ;
    }
    b.append(" --app-container-manager ").append(this.appContainerManager) ;
    b.append(" --app-max-memory ").append(this.appMaxMemory) ;
    b.append(" --app-num-of-worker ").append(this.appNumOfWorkers) ;
    
    if(worker != null) {
      b.append(" --worker-class ").append(this.worker) ;
    }
    
    b.append(" --worker-max-memory ").append(this.workerMaxMemory) ;
    b.append(" --worker-num-of-core ").append(this.workerNumOfCore) ;
    
    for(Map.Entry<String, String> entry : yarnConf.entrySet()) {
      b.append(" --conf:").append(entry.getKey()).append("=").append(entry.getValue()) ;
    }
  }
}