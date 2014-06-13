package com.neverwinterdp.hadoop.yarn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.yarn.api.ApplicationConstants;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;

public class AppOptions {
  @Parameter(names = "--mini-cluster-env", description = "Setup the mini cluster env for testing")
  boolean miniClusterEnv = false ;
  
  @Parameter(names = "--jar", description = "Command list to launch the container")
  public String jarFile  ;
  
  @Parameter(names = "--name", description = "The application name")
  public String appName = "Yarn Application"  ;
  
  @Parameter(names = "--container-manager", description = "The application container manager class")
  public String containerManager   ;
  
  @DynamicParameter(names = "--conf:", description = "The yarn configuration overrided properties")
  Map<String, String> conf = new HashMap<String, String>()  ;
  
  public List<String> buildAppMasterCommands() {
    List<String> holder = new ArrayList<String>() ;
    StringBuilder b = new StringBuilder() ;
    b.append("java ").append(AppMaster.class.getName()) ;
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
}