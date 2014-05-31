package com.neverwinterdp.server.shell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.server.command.ServerCommand;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.command.ServerModuleCommands;
import com.neverwinterdp.server.module.ModuleRegistration;
import com.neverwinterdp.server.module.ModuleRegistration.InstallStatus;
import com.neverwinterdp.server.module.ModuleRegistration.RunningStatus;
import com.neverwinterdp.util.text.TabularPrinter;

@CommandGroupConfig(name = "module")
public class ModuleCommandGroup extends CommandGroup {
  public ModuleCommandGroup() {
    add("list", ModuleList.class);
    add("install", Install.class);
    add("uninstall", Uninstall.class);
  }
  
  @Parameters(commandDescription = "execute various module list option such available, installed")
  static public class ModuleList extends Command {
    @Parameter(names = {"--available"}, description = "List the available modules")
    boolean available ;
    
    @Parameter(names = {"--installed"}, description = "List the installed modules")
    boolean installed ;
    
    @ParametersDelegate
    MemberSelectorOption memberSelector = new MemberSelectorOption();
    
    public void execute(ShellContext ctx) {
      ClusterMember[] members = memberSelector.getMembers(ctx) ;
      if(available) {
        list(ctx, new ServerModuleCommands.GetAvailableModule(), members, "List available") ;
      }
      if(installed) {
        list(ctx, new ServerModuleCommands.GetInstallModule(), members, "List installed") ;
      }
    }
  }
  
  @Parameters(commandDescription = "execute module install options")
  static public class Install extends Command {
    @Parameter(names = {"--autostart"}, description = "Autostart after install")
    boolean autostart = false ;
    
    @DynamicParameter(names = "-M", description = "Module properties")
    Map<String, String> properties = new HashMap<String, String>();

    @Parameter(description = "List of module to install")
    List<String> modules = new ArrayList<String>() ;

    @ParametersDelegate
    MemberSelectorOption memberSelector = new MemberSelectorOption();
    
    public void execute(ShellContext ctx) {
      if(modules.size() > 0) {
        ClusterMember[] members = memberSelector.getMembers(ctx) ;
        ServerCommand<ModuleRegistration[]> installCommand = 
            new ServerModuleCommands.InstallModule(modules, autostart, properties) ;
        list(ctx, installCommand, members, "Install") ;
      }
    }
  }
  
  @Parameters(commandDescription = "execute various module uninstall options")
  static public class Uninstall extends Command {
    @Parameter(description = "List of module to uninstall")
    List<String> modules = new ArrayList<String>() ;
    
    @Parameter(names = {"--timeout"}, description = "timeout")
    long timeout = 5000 ;
    
    @ParametersDelegate
    MemberSelectorOption memberSelector = new MemberSelectorOption();
    
    public void execute(ShellContext ctx) {
      if(modules.size() > 0) {
        ClusterMember[] members = memberSelector.getMembers(ctx) ;
        ServerCommand<ModuleRegistration[]> uninstallCommand = 
            new ServerModuleCommands.UninstallModule(modules) ;
        uninstallCommand.setTimeout(timeout) ;
        list(ctx, uninstallCommand, members, "Uninstall") ;
      }
    }
  }
  
  static void list(ShellContext ctx, ServerCommand<ModuleRegistration[]> command, ClusterMember[] members, String title) {
    ClusterClient client = ctx.getClusterClient() ;
    ServerCommandResult<ModuleRegistration[]>[] results = null ;
    if(members == null) {
      results = client.execute(command) ; 
    } else {
      results = client.execute(command, members) ;
    }
    
    for(int i = 0; i < results.length; i++) {
      ServerCommandResult<ModuleRegistration[]> sel = results[i] ;
      ctx.console().header(title + " on member " + sel.getFromMember());
      if(sel.hasError()) {
        ctx.console().println("   ", "ERROR") ;
        ctx.console().println(sel.getError()) ;
      } else {
        TabularPrinter printer = ctx.console().tabularPrinter(30, 10, 10) ;
        printer.setIndent("  ") ;
        printer.header("Module", "Install", "Status");
        ModuleRegistration[] mstatus = sel.getResult() ;
        for(ModuleRegistration selStatus : mstatus) {
          String moduleName = selStatus.getModuleName() ;
          InstallStatus installStatus = selStatus.getInstallStatus() ;
          RunningStatus runningStatus = selStatus.getRunningStatus() ;
          printer.row(moduleName, installStatus, runningStatus);
        }
      }
    }
  }
}