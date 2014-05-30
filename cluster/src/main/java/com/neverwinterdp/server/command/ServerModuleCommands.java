package com.neverwinterdp.server.command;

import java.util.List;

import com.neverwinterdp.server.ModuleStatus;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.util.text.StringUtil;

public class ServerModuleCommands {
  static public class GetAvailableModule extends ServerCommand<ModuleStatus[]> {
    public ModuleStatus[] execute(Server server) throws Exception {
      return server.getModuleContainer().getAvailableModules() ;
    }
  }
  
  static public class GetInstallModule extends ServerCommand<ModuleStatus[]> {
    public ModuleStatus[] execute(Server server) throws Exception {
      return server.getModuleContainer().getInstalledModules() ;
    }
  }
  
  static public class InstallModule extends ServerCommand<ModuleStatus[]> {
    private List<String> modules ;
    private boolean autostart =false ;
    
    public InstallModule() {} 
    
    public InstallModule(List<String> modules, boolean autostart) {
      this.modules = modules ;
      this.autostart = autostart ;
    }
    
    public ModuleStatus[] execute(Server server) throws Exception {
      String[] moduleNames = StringUtil.toArray(modules) ;
      ModuleStatus[] status = server.getModuleContainer().install(moduleNames) ;
      if(autostart) {
        status = server.getModuleContainer().start(moduleNames) ;
      }
      return status ;
    }
  }
  
  static public class UninstallModule extends ServerCommand<ModuleStatus[]> {
    private List<String> modules ;
    
    public UninstallModule() {} 
    
    public UninstallModule(List<String> modules) {
      this.modules = modules ;
    }
    
    public ModuleStatus[] execute(Server server) throws Exception {
      String[] moduleNames = StringUtil.toArray(modules) ;
      return server.getModuleContainer().uninstall(moduleNames) ;
    }
  }
}