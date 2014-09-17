package com.neverwinterdp.server.gateway;

import com.neverwinterdp.server.ServerState;
import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.command.ServerCommand;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.command.ServerCommands;
import com.neverwinterdp.yara.MetricRegistry;
import com.neverwinterdp.yara.cluster.ClusterMetricRegistry;
import com.neverwinterdp.yara.snapshot.ClusterMetricRegistrySnapshot;
import com.neverwinterdp.yara.snapshot.MetricRegistrySnapshot;

public class ServerCommandPlugin extends CommandPlugin {
  public ServerCommandPlugin() {
    add("ping", new ping());
    add("registration", new registration())  ;
    add("metric", new metric()) ;
    add("metric-snapshot", new metricSnapshot()) ;
    add("metric-clear", new metricClear()) ;
    add("metric-cluster", new metricCluster()) ;
    add("metric-cluster-snapshot", new metricClusterSnapshot()) ;
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
      ServerCommand<MetricRegistry> serverCmd = new ServerCommands.GetMetricRegistry() ;
      command.mapPartial(serverCmd);
      return command.getMemberSelector().execute(clusterClient, serverCmd) ;
    }
  }
  
  static public class metricSnapshot implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServerCommand<MetricRegistrySnapshot> serverCmd = new ServerCommands.GetMetricRegistrySnapshot() ;
      command.mapPartial(serverCmd);
      return command.getMemberSelector().execute(clusterClient, serverCmd) ;
    }
  }
  
  static public class metricCluster implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServerCommand<MetricRegistry> serverCmd = new ServerCommands.GetMetricRegistry() ;
      command.mapPartial(serverCmd);
      ServerCommandResult<MetricRegistry>[] results = command.getMemberSelector().execute(clusterClient, serverCmd) ;
      ClusterMetricRegistry registry = new ClusterMetricRegistry() ;
      for(ServerCommandResult<MetricRegistry> result : results) {
        registry.update(result.getResult());
      }
      return registry ;
    }
  }
  
  static public class metricClusterSnapshot implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServerCommand<MetricRegistry> serverCmd = new ServerCommands.GetMetricRegistry() ;
      command.mapPartial(serverCmd);
      ServerCommandResult<MetricRegistry>[] results = command.getMemberSelector().execute(clusterClient, serverCmd) ;
      ClusterMetricRegistry registry = new ClusterMetricRegistry() ;
      for(ServerCommandResult<MetricRegistry> result : results) {
        registry.update(result.getResult());
      }
      return new ClusterMetricRegistrySnapshot(registry) ;
    }
  }
  
  static public class metricClear implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServerCommand<Integer> serverCmd = new ServerCommands.ClearMetricRegistry() ;
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
      ServerCommand<ServerState> shutdown = new ServerCommands.Shutdown() ;
      return command.getMemberSelector().execute(clusterClient, shutdown) ;
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
