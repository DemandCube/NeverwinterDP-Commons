package com.neverwinterdp.hadoop.yarn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.yarn.api.ApplicationConstants;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;

public class AppOptions {
  @Parameter(names = "--jar", description = "Command list to launch the container")
  public String jarFile  ;
  
  @Parameter(names = "--app-name", description = "The application name")
  public String appName = "Yarn Application"  ;
  
  @Parameter(names = "--app-main", description = "The application class")
  public String appMain   ;
  
  @Parameter(names = "--app-allocate-container", description = "Allocte number of container for the application")
  public int allocateContainer = 1;
  
  @DynamicParameter(names = "--cmd:", description = "Command list to launch the container")
  Map<String, String> cmd = new HashMap<String, String>()  ;
  
  @Parameter(names = "--mini-cluster-env", description = "Setup the mini cluster env for testing")
  boolean miniClusterEnv = false ;
  
  public List<String> buildAppMasterCommands() {
    List<String> holder = new ArrayList<String>() ;
    StringBuilder b = new StringBuilder() ;
    b.append("java ").append(appMain) ;
    b.append(" --app-allocate-container " + allocateContainer);
    for(Map.Entry<String, String> entry : cmd.entrySet()) {
      b.append(" --cmd:").append(entry.getKey()).append("=").append(entry.getValue()) ;
    }
    b.append(" 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout") ; 
    b.append(" 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr") ;
    holder.add(b.toString()) ;
    System.out.println("Master Command: " + b.toString());
    return holder ;
  }
  
  public List<String> buildAppCommands() {
    List<String> holder = new ArrayList<String>() ;
    StringBuilder b = new StringBuilder() ;
    for(String selCommand : cmd.values()) {
      if(selCommand.startsWith("\"") && selCommand.endsWith("\"")) {
        selCommand = selCommand.substring(1, selCommand.length() - 1) ;
      }
      b.append(selCommand).append(" ");
    }
    b.append(" 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout") ; 
    b.append(" 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr") ;
    holder.add(b.toString()) ;
    System.out.println("App Command: " + b.toString());
    return holder ;
  }
  
  public String getCommand(String name) {
    String value = cmd.get(name) ;
    if(value == null) return null ;
    if(value.startsWith("\"") && value.endsWith("\"")) {
      value = value.substring(1, value.length() - 1) ;
    }
    return value ;
  }
}