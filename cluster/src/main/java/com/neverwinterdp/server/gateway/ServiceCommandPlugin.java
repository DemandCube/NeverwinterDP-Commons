package com.neverwinterdp.server.gateway;

import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.command.ServiceCommand;
import com.neverwinterdp.server.command.ServiceCommands;
import com.neverwinterdp.server.service.ServiceRegistration;

@CommandPluginConfig(name = "service")
public class ServiceCommandPlugin extends CommandPlugin {
  public ServiceCommandPlugin() {
    add("cleanup", new Cleanup());
    add("start", new Start());
    add("stop", new Stop());
    add("restart", new Restart());
    add("configure", new Configure());
  }
  
  static public class Cleanup implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServiceCommand<Boolean> cleanup = new ServiceCommands.Cleanup() ;
      command.mapAll(cleanup) ;
      return command.getMemberSelector().execute(clusterClient, cleanup) ;
    }
  }
  
  static public class Start implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServiceCommand<ServiceRegistration> start = new ServiceCommands.Start() ;
      command.mapAll(start) ;
      return command.getMemberSelector().execute(clusterClient, start) ;
    }
  }
  
  static public class Stop implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServiceCommand<ServiceRegistration> stop = new ServiceCommands.Stop() ;
      command.mapAll(stop) ;
      return command.getMemberSelector().execute(clusterClient, stop) ;
    }
  }
  
  static public class Restart implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServiceCommand<ServiceRegistration> restart = new ServiceCommands.Restart() ;
      command.mapAll(restart) ;
      return command.getMemberSelector().execute(clusterClient, restart) ;
    }
  }
  
  static public class Configure implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServiceCommand<Boolean> configure = new ServiceCommands.Configure() ;
      command.mapAll(configure) ;
      return command.getMemberSelector().execute(clusterClient, configure) ;
    }
  }
}