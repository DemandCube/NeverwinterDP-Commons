package com.neverwinterdp.hadoop.yarn;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.yarn.api.ApplicationConstants;

import com.beust.jcommander.Parameter;

public class AppOptions {
  @Parameter(names = "--jar", description = "Command list to launch the container")
  String jarFile  ;
  
  @Parameter(names = "--app-name", description = "The application name")
  String appName = "Yarn Application"  ;
  
  
  @Parameter(names = "--command", description = "Command list to launch the container")
  List<String> commands = new ArrayList<String>()  ;
  
  @Parameter(names = "--mini-cluster-env", description = "Setup the mini cluster env for testing")
  boolean miniClusterEnv = false ;
  
  public List<String> buildCommands() {
    List<String> holder = new ArrayList<String>() ;
    for(String command : commands) {
      command +=  " 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout" + 
                  " 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr" ;
      holder.add(command) ;
    }
    return holder ;
  }
}
