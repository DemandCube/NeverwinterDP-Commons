package com.neverwinterdp.server.gateway;

import com.neverwinterdp.server.ServerState;
import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.command.ServerCommand;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.command.ServerCommands;
import com.neverwinterdp.util.monitor.snapshot.ApplicationMonitorSnapshot;

public class ServerCommandPlugin extends CommandPlugin {
  public ServerCommandPlugin() {
    add("ping", new ping());
    add("registration", new registration())  ;
    add("metric", new metric()) ;
    add("metric-clear", new metricClear()) ;
    add("start", new start()) ;
    add("shutdown", new shutdown()) ;
    add("exit", new exit())  ;
    add("jvminfo", new jvminfo()) ;
  }
  
  static public class ping implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServerCommand<ServerState> ping = new ServerCommands.Ping() ;
      ServerCommandResult<ServerState>[] results = command.getMemberSelector().execute(clusterClient, ping) ;
      return results ;
    }
  }
  
  static public class registration implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServerCommands.GetServerRegistration serverCmd = new ServerCommands.GetServerRegistration() ;
      return command.getMemberSelector().execute(clusterClient, serverCmd) ;
    }
  }
  
  static public class metric implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServerCommand<ApplicationMonitorSnapshot> serverCmd = new ServerCommands.GetMonitorSnapshot() ;
      command.mapPartial(serverCmd);
      return command.getMemberSelector().execute(clusterClient, serverCmd) ;
    }
  }
  
  static public class metricClear implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServerCommand<Integer> serverCmd = new ServerCommands.ClearMonitor() ;
      command.mapPartial(serverCmd);
      return command.getMemberSelector().execute(clusterClient, serverCmd) ;
    }
  }
  
  static public class start implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServerCommand<ServerState> start = new ServerCommands.Start() ;
      return command.getMemberSelector().execute(clusterClient, start) ;
    }
  }
  
  static public class shutdown implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServerCommand<ServerState> ping = new ServerCommands.Shutdown() ;
      return command.getMemberSelector().execute(clusterClient, ping) ;
    }
  }
    
  static public class exit implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServerCommand<ServerState> ping = new ServerCommands.Exit() ;
      return command.getMemberSelector().execute(clusterClient, ping) ;
    }
  }
  
  static public class jvminfo implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServerCommands.GetJVMInfo cmd = new ServerCommands.GetJVMInfo() ;
      return command.getMemberSelector().execute(clusterClient, cmd) ;
    }
  }
}