package com.neverwinterdp.server.command;

import java.util.List;
import java.util.Map;

import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.module.ModuleRegistration;
import com.neverwinterdp.util.text.StringUtil;

public class ServerModuleCommands {
  static public class GetAvailableModule extends ServerCommand<ModuleRegistration[]> {
    public ModuleRegistration[] execute(Server server) throws Exception {
      return server.getModuleContainer().getAvailableModules() ;
    }
  }
  
  static public class GetInstallModule extends ServerCommand<ModuleRegistration[]> {
    public ModuleRegistration[] execute(Server server) throws Exception {
      return server.getModuleContainer().getInstalledModules() ;
    }
  }
  
  static public class InstallModule extends ServerCommand<ModuleRegistration[]> {
    private List<String> modules ;
    private boolean autostart =false ;
    private Map<String, String> properties ;
    
    public InstallModule() {} 
    
    public InstallModule(List<String> modules, boolean autostart, Map<String, String> properties) {
      this.modules = modules ;
      this.autostart = autostart ;
      this.properties = properties ;
    }
    
    public ModuleRegistration[] execute(Server server) throws Exception {
      String[] moduleNames = StringUtil.toArray(modules) ;
      ModuleRegistration[] status = server.getModuleContainer().install(properties, moduleNames) ;
      if(autostart) {
        status = server.getModuleContainer().start(moduleNames) ;
      }
      return status ;
    }
  }
  
  static public class UninstallModule extends ServerCommand<ModuleRegistration[]> {
    private List<String> modules ;
    
    public UninstallModule() {} 
    
    public UninstallModule(List<String> modules) {
      this.modules = modules ;
    }
    
    public ModuleRegistration[] execute(Server server) throws Exception {
      String[] moduleNames = StringUtil.toArray(modules) ;
      return server.getModuleContainer().uninstall(moduleNames) ;
    }
  }
}