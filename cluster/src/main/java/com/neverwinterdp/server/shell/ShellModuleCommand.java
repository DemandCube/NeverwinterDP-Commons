package com.neverwinterdp.server.shell;

import com.beust.jcommander.Parameters;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.gateway.Command;
import com.neverwinterdp.server.module.ModuleRegistration;
import com.neverwinterdp.server.module.ModuleRegistration.InstallStatus;
import com.neverwinterdp.server.module.ModuleRegistration.RunningStatus;
import com.neverwinterdp.util.text.TabularPrinter;

@ShellCommandConfig(name = "module")
public class ShellModuleCommand extends ShellCommand {
  public ShellModuleCommand() {
    add("list", ModuleList.class);
    add("install", Install.class);
    add("uninstall", Uninstall.class);
  }
  
  @Parameters(commandDescription = "execute various module list option such available, installed")
  static public class ModuleList extends ShellSubCommand {
    public void execute(ShellContext ctx, Command command) throws Exception {
      ServerCommandResult<ModuleRegistration[]>[] results =  ctx.getClusterGateway().execute(command) ;
      list(ctx, results, "List installed") ;
    }
  }
  
  @Parameters(commandDescription = "execute module install options")
  static public class Install extends ShellSubCommand {
    public void execute(ShellContext ctx, Command command) throws Exception {
      ServerCommandResult<ModuleRegistration[]>[] results = ctx.getClusterGateway().execute(command) ;
      list(ctx, results, "Install") ;
    }
  }
  
  @Parameters(commandDescription = "execute various module uninstall options")
  static public class Uninstall extends ShellSubCommand {
    public void execute(ShellContext ctx, Command command) throws Exception {
      ServerCommandResult<ModuleRegistration[]>[] results = ctx.getClusterGateway().execute(command) ;
      list(ctx, results, "Uninstall") ;
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