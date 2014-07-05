package com.neverwinterdp.server.gateway;

import com.neverwinterdp.server.ServerState;
import com.neverwinterdp.server.command.ServerCommand;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.command.ServerCommands;
import com.neverwinterdp.util.monitor.snapshot.ApplicationMonitorSnapshot;

public class Server extends Plugin {
  protected Object doCall(String commandName, CommandParams params) throws Exception {
    ServerCommandResult<?>[] results = null ;
    if("ping".equals(commandName)) results = ping(params) ;
    else if("metric".equals(commandName)) results = metric(params) ;
    else if("clearMetric".equals(commandName)) results = clearMetric(params) ;
    else if("start".equals(commandName)) results = start(params) ;
    else if("shutdown".equals(commandName)) results = shutdown(params) ;
    else if("exit".equals(commandName)) results = exit(params) ;
    if(results != null) return results ;
    return null ;
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
    String expression = params.getString("filter") ;
    return metric(memberSelector, expression) ;
  }
  
  public ServerCommandResult<ApplicationMonitorSnapshot>[] metric(MemberSelector memberSelector, String filter) {
    ServerCommand<ApplicationMonitorSnapshot> cmd = new ServerCommands.GetMonitorSnapshot(filter) ;
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