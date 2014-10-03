package com.neverwinterdp.server.gateway;

import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.command.ServiceCommand;
import com.neverwinterdp.server.command.ServiceCommands;
import com.neverwinterdp.server.yara.YaraServerService;
import com.neverwinterdp.yara.snapshot.ClusterMetricRegistrySnapshot;

@CommandPluginConfig(name = "yara")
public class YaraCommandPlugin extends CommandPlugin {
  public YaraCommandPlugin() {
    add("snapshot", new snapshot()) ;
  }
  
  static public class snapshot implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServiceCommand<ClusterMetricRegistrySnapshot> methodCall = 
          new ServiceCommands.MethodCall<ClusterMetricRegistrySnapshot>("getClusterMetricRegistrySnapshot") ;
      methodCall.setTargetService("YaraServer", YaraServerService.class.getSimpleName());
      return command.getMemberSelector().execute(clusterClient, methodCall) ;
    }
  }
}