package com.neverwinterdp.hadoop.yarn.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.ApplicationConstants;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;

public class AppMasterConfig {
  @Parameter(names = "--mini-cluster-env", description = "Setup the mini cluster env for testing")
  boolean miniClusterEnv = false ;
  
  @Parameter(names = "--jar", description = "Command list to launch the container")
  public String jarFile  ;
  
  @Parameter(names = "--app-home", description = "The shared location of the application directory")
  public String appHome ;
  
  @Parameter(
    names = "--upload-app", 
    description = "The local app home on the client, that should be copy and deploy to app home"
  )
  public String uploadApp ;
  
  @Parameter(names = "--app-name", description = "The application name")
  public String appName = "Yarn Application"  ;
  
  @Parameter(names = "--app-host-name", description = "The application host name")
  public String appHostName = ""  ;
  
  @Parameter(names = "--app-rpc-port", description = "The application rpc port")
  public int appRpcPort = 0  ;
  
  @Parameter(names = "--app-tracking-url", description = "The application tracking url")
  public String appTrackingUrl = ""  ;
  
  
  @Parameter(names = "--container-manager", description = "The application container manager class")
  public String containerManager   ;
  
  @Parameter(names = "--max-memory", description = "Maximum amount of memory allocate to jvm")
  public int maxMemory = 128 ;
  
  @DynamicParameter(names = "--conf:", description = "The yarn configuration overrided properties")
  public Map<String, String> conf = new HashMap<String, String>()  ;
  
  public List<String> buildAppMasterCommands() {
    List<String> holder = new ArrayList<String>() ;
    StringBuilder b = new StringBuilder() ;
    b.append("java ").append(" -Xmx" + maxMemory + "m ").
      append(AppMaster.class.getName()) ;
    b.append(" --container-manager " + containerManager);
    for(Map.Entry<String, String> entry : conf.entrySet()) {
      b.append(" --conf:").append(entry.getKey()).append("=").append(entry.getValue()) ;
    }
    b.append(" 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout") ; 
    b.append(" 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr") ;
    holder.add(b.toString()) ;
    System.out.println("Master Command: " + b.toString());
    return holder ;
  }
  
  public void overrideConfiguration(Configuration aconf) {
    for(Map.Entry<String, String> entry : conf.entrySet()) {
      aconf.set(entry.getKey(), entry.getValue()) ;
    }
  }
}