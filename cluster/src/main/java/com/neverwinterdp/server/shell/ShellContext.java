package com.neverwinterdp.server.shell;

import java.util.HashMap;
import java.util.Map;

import com.neverwinterdp.server.client.Cluster;

public class ShellContext {
  private Map<String, Object> variables = new HashMap<String, Object>() ;
  private Console console ;
  private Cluster cluster ;
  private ExecuteContext currentExecuteContext ;
  private ExecuteContext lastExecuteContext ;
  
  public ShellContext() {
    console = new Console(System.out) ;
  }

  public Map<String, Object> getVariables() { return this.variables ; }
  
  public Console console() { return this.console ; }
  
  public Cluster getCluster() { return this.cluster ; }
  
  public ExecuteContext getExecuteContext() { return this.currentExecuteContext ; }
  
  public ExecuteContext getLastExecuteContext() { return this.lastExecuteContext ; }
  
  public void connect(String ... members) {
    if(cluster == null) cluster = new Cluster(members) ;
    else cluster.connect(members);
  }
  
  public void onStartCommand(CommandGroup group, Command command, String[] args) {
    lastExecuteContext = currentExecuteContext ;
    currentExecuteContext = new ExecuteContext() ;
    console.newConsoleOutput() ;
  }
  
  public void onFinishCommand(CommandGroup group, Command command) {
    currentExecuteContext.setConsoleOutput(console.getTextOutput()) ;
  }
  
  public void close() {
    if(cluster != null) cluster.close(); 
  }
} 
