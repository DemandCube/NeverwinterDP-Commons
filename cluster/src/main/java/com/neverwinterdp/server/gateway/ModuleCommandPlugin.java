package com.neverwinterdp.server.gateway;

import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.command.ServerCommand;
import com.neverwinterdp.server.command.ServerModuleCommands;
import com.neverwinterdp.server.module.ModuleRegistration;

public class ModuleCommandPlugin extends CommandPlugin {
  public ModuleCommandPlugin() {
    add("list", new List()) ;
    add("list-installed", new ListInstalled()) ;
    add("install", new Install()) ;
    add("uninstall", new Uninstall()) ;
  }
  
  static public class List implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      MemberSelector memberSelector = command.getMemberSelector() ;
      ServerCommand<ModuleRegistration[]> serverCmd = new ServerModuleCommands.GetAvailableModule() ;
      return memberSelector.execute(clusterClient, serverCmd) ;
    }
  }
  
  static public class ListInstalled implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      MemberSelector memberSelector = command.getMemberSelector() ;
      ServerCommand<ModuleRegistration[]> serverCmd = new ServerModuleCommands.GetInstallModule() ;
      return memberSelector.execute(clusterClient, serverCmd) ;
    }
  }
  
  static public class Install implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServerCommand<ModuleRegistration[]> serverCmd = new ServerModuleCommands.InstallModule() ;
      command.mapAll(serverCmd);
      return command.getMemberSelector().execute(clusterClient, serverCmd) ;
    }
  }
  
  static public class Uninstall implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServerCommand<ModuleRegistration[]> serverCmd = new ServerModuleCommands.UninstallModule() ;
      command.mapPartial(serverCmd);
      return command.getMemberSelector().execute(clusterClient, serverCmd) ;
    }
  }
}