package com.neverwinterdp.server.shell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.neverwinterdp.server.client.MemberSelector;
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
    @Parameter(names = {"--type"}, description = "List the available modules")
    String type ;
    
    @ParametersDelegate
    MemberSelector memberSelector = new MemberSelector();
    
    public void execute(ShellContext ctx) {
      list(ctx, ctx.getCluster().module.list(memberSelector, type), "List installed") ;
    }
  }
  
  @Parameters(commandDescription = "execute module install options")
  static public class Install extends Command {
    @Parameter(names = {"--autostart"}, description = "Autostart after install")
    boolean autostart = false ;
    
    @DynamicParameter(names = "-P", description = "Module properties")
    Map<String, String> properties = new HashMap<String, String>();

    @Parameter(
        names = { "--module" }, variableArity = true, 
        description = "List of module to install"
    )
    List<String> modules = new ArrayList<String>() ;

    @ParametersDelegate
    MemberSelector memberSelector = new MemberSelector();
    
    public void execute(ShellContext ctx) {
      list(ctx, ctx.getCluster().module.install(memberSelector, modules, autostart, properties), "Install") ;
    }
  }
  
  @Parameters(commandDescription = "execute various module uninstall options")
  static public class Uninstall extends Command {
    @Parameter(
        names = { "--module" }, variableArity = true,
        description = "List of module to uninstall"
    )
    List<String> modules = new ArrayList<String>() ;
    
    @Parameter(names = {"--timeout"}, description = "timeout")
    long timeout = 5000 ;
    
    @ParametersDelegate
    MemberSelector memberSelector = new MemberSelector();
    
    public void execute(ShellContext ctx) {
      list(ctx, ctx.getCluster().module.uninstall(memberSelector, modules), "Uninstall") ;
    }
  }
  
  static void list(ShellContext ctx, ServerCommandResult<ModuleRegistration[]>[] results, String title) {
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