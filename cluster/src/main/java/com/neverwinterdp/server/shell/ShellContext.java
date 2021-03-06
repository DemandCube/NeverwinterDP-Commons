package com.neverwinterdp.server.shell;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import com.neverwinterdp.server.gateway.ClusterGateway;

public class ShellContext {
  private Map<String, Object> variables = new HashMap<String, Object>() ;
  private Console console ;
  private Timer   timer ;
  private ClusterGateway cluster ;
  private ExecuteContext currentExecuteContext ;
  private ExecuteContext lastExecuteContext ;
  
  public ShellContext() {
    console = new Console(System.out) ;
  }
  
  public Map<String, Object> getVariables() { return this.variables ; }
  
  public Console console() { return this.console ; }
  
  public Timer getTimer() {
    if(timer == null) timer = new Timer() ;
    return this.timer ; 
  }
  
  public ClusterGateway getClusterGateway() { return this.cluster ; }
  
  public ExecuteContext getExecuteContext() { return this.currentExecuteContext ; }
  
  public ExecuteContext getLastExecuteContext() { return this.lastExecuteContext ; }
  
  public void connect(String ... members) {
    if(cluster == null) cluster = new ClusterGateway(members) ;
    else cluster.connect(members);
  }
  
  public void onStartCommand(ShellCommand shellCommand, ShellSubCommand shellSubCommand) {
    lastExecuteContext = currentExecuteContext ;
    currentExecuteContext = new ExecuteContext() ;
    console.newConsoleOutput() ;
  }
  
  public void onFinishCommand(ShellCommand group, ShellSubCommand command) {
    currentExecuteContext.setConsoleOutput(console.getTextOutput()) ;
  }
  
  public boolean isClose() { return cluster == null ; }
  
  public void terminateTimer() {
    if(timer != null) {
      timer.cancel() ;
      timer.purge() ;
      timer = null ;
    }
  }
  
  public void close() {
    terminateTimer() ;
    if(cluster != null) {
      cluster.close(); 
      cluster = null ;
    }
  }
} 