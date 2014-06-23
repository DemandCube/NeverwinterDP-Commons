package com.neverwinterdp.server.client;

import com.neverwinterdp.server.ServerState;
import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.command.ServerCommand;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.command.ServerCommands;
import com.neverwinterdp.util.JSONSerializer;
import com.neverwinterdp.util.monitor.snapshot.ApplicationMonitorSnapshot;

public class Server {
  private ClusterClient clusterClient ;
  
  public Server(ClusterClient clusterClient) {
    this.clusterClient = clusterClient ;
  }
  
  public String call(String json) {
    try {
      CommandParams params = JSONSerializer.INSTANCE.fromString(json, CommandParams.class) ;
      String commandName = params.getString("_commandName") ;
      ServerCommandResult<?>[] results = null ;
      if("ping".equals(commandName)) results = ping(params) ;
      else if("metric".equals(commandName)) results = metric(params) ;
      else if("clearMetric".equals(commandName)) results = clearMetric(params) ;
      else if("start".equals(commandName)) results = start(params) ;
      else if("shutdown".equals(commandName)) results = shutdown(params) ;
      else if("exit".equals(commandName)) results = exit(params) ;
      if(results != null) return JSONSerializer.INSTANCE.toString(results) ;
      return "{ 'success': false, 'message': 'unknown command'}" ;
    } catch(Throwable t) {
      t.printStackTrace();
      throw t ;
    }
  }

  public ServerCommandResult<ServerState>[] ping(CommandParams params) {
    MemberSelector memberSelector = new MemberSelector(params) ;
    return ping(memberSelector) ;
  }
  
  public ServerCommandResult<ServerState>[] ping(MemberSelector memberSelector) {
    ServerCommand<ServerState> ping = new ServerCommands.Ping() ;
    ServerCommandResult<ServerState>[] results = memberSelector.execute(clusterClient, ping) ;
    return results ;
  }
  
  public ServerCommandResult<ApplicationMonitorSnapshot>[] metric(CommandParams params) {
    MemberSelector memberSelector = new MemberSelector(params) ;
    return metric(memberSelector) ;
  }
  
  public ServerCommandResult<ApplicationMonitorSnapshot>[] metric(MemberSelector memberSelector) {
    ServerCommand<ApplicationMonitorSnapshot> cmd = new ServerCommands.GetMonitorSnapshot() ;
    return memberSelector.execute(clusterClient, cmd) ;
  }
  
  public ServerCommandResult<Integer>[] clearMetric(CommandParams params) {
    MemberSelector memberSelector = new MemberSelector(params) ;
    String expression = params.getString("expression") ;
    return clearMetric(memberSelector, expression) ;
  }
  
  public ServerCommandResult<Integer>[] clearMetric(MemberSelector memberSelector, String nameExp) {
    ServerCommand<Integer> cmd = new ServerCommands.ClearMonitor(nameExp) ;
    return memberSelector.execute(clusterClient, cmd) ;
  }
  
  public ServerCommandResult<ServerState>[] start(CommandParams params) {
    MemberSelector memberSelector = new MemberSelector(params) ;
    return start(memberSelector) ;
  }
  
  public ServerCommandResult<ServerState>[] start(MemberSelector memberSelector) {
    ServerCommand<ServerState> ping = new ServerCommands.Start() ;
    ServerCommandResult<ServerState>[] results = memberSelector.execute(clusterClient, ping) ;
    return results ;
  }
  
  public ServerCommandResult<ServerState>[] shutdown(CommandParams params) {
    MemberSelector memberSelector = new MemberSelector(params) ;
    return shutdown(memberSelector) ;
  }
  
  public ServerCommandResult<ServerState>[] shutdown(MemberSelector memberSelector) {
    ServerCommand<ServerState> ping = new ServerCommands.Shutdown() ;
    ServerCommandResult<ServerState>[] results = memberSelector.execute(clusterClient, ping) ;
    return results ;
  }
  
  public ServerCommandResult<ServerState>[] exit(CommandParams params) {
    MemberSelector memberSelector = new MemberSelector(params) ;
    return exit(memberSelector) ;
  }
  
  public ServerCommandResult<ServerState>[] exit( MemberSelector memberSelector) {
    ServerCommand<ServerState> ping = new ServerCommands.Exit() ;
    ServerCommandResult<ServerState>[] results = memberSelector.execute(clusterClient, ping) ;
    return results ;
  }
}