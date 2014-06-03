package com.neverwinterdp.server.shell;

import java.util.HashMap;
import java.util.Map;

import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.cluster.hazelcast.HazelcastClusterClient;

public class ShellContext {
  private Map<String, Object> variables = new HashMap<String, Object>() ;
  private Console console ;
  private ClusterClient client ;
  private ExecuteContext currentExecuteContext ;
  private ExecuteContext lastExecuteContext ;
  
  public ShellContext() {
    console = new Console(System.out) ;
  }

  public Map<String, Object> getVariables() { return this.variables ; }
  
  public Console console() { return this.console ; }
  
  public ClusterClient getClusterClient() { return this.client ; }
  
  public ExecuteContext getExecuteContext() { return this.currentExecuteContext ; }
  
  public ExecuteContext getLastExecuteContext() { return this.lastExecuteContext ; }
  
  public void connect(String ... members) {
    if(client != null) client.shutdown();
    client = new HazelcastClusterClient(members) ;
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
    if(client != null) client.shutdown();
  }
} 
