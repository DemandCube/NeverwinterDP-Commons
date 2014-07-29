package com.neverwinterdp.server.shell;

import com.neverwinterdp.server.command.ServiceCommandResult;
import com.neverwinterdp.server.gateway.Command;
import com.neverwinterdp.util.text.TabularPrinter;

@ShellCommandConfig(name = "service")
public class ShellServiceCommand extends ShellCommand {
  public ShellServiceCommand() {
    add("cleanup", Cleanup.class);
    add("start", Start.class);
    add("stop", Stop.class);
    add("restart", Restart.class);
    add("configure", Configure.class);
  }
  
  static public class Cleanup extends ShellSubCommand {
    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      ServiceCommandResult<Boolean>[] results = ctx.getClusterGateway().execute(command) ;
      ctx.console().header(command.getCommandLine());
      printPrimitiveResults(results, ctx);
    }
  }
  
  static public class Start extends ShellSubCommand {
    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      ServiceCommandResult<Boolean>[] results = ctx.getClusterGateway().execute(command) ;
      ctx.console().header(command.getCommandLine());
      printPrimitiveResults(results, ctx);
    }
  }
  
  static public class Stop extends ShellSubCommand {
    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      ServiceCommandResult<Boolean>[] results = ctx.getClusterGateway().execute(command) ;
      ctx.console().header(command.getCommandLine());
      printPrimitiveResults(results, ctx);
    }
  }
  
  static public class Restart extends ShellSubCommand {
    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      ServiceCommandResult<Boolean>[] results = ctx.getClusterGateway().execute(command) ;
      ctx.console().header(command.getCommandLine());
      printPrimitiveResults(results, ctx);
    }
  }
  
  static public class Configure extends ShellSubCommand {
    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      ServiceCommandResult<Boolean>[] results = ctx.getClusterGateway().execute(command) ;
      ctx.console().header(command.getCommandLine());
      printPrimitiveResults(results, ctx);
    }
  }
  
  static void printPrimitiveResults(ServiceCommandResult<?>[] results, ShellContext ctx) {
    TabularPrinter tprinter = ctx.console().tabularPrinter(30, 10, 10) ;
    tprinter.header("Host IP", "Port", "Result");
    for(int i = 0; i < results.length; i++) {
      ServiceCommandResult<?> sel = results[i] ;
      String host = sel.getFromMember().getIpAddress() ;
      int    port = sel.getFromMember().getPort() ;
      Object result = sel.getResult() ;
      tprinter.row(host, port, result);
    }
    
    for(int i = 0; i < results.length; i++) {
      ServiceCommandResult<?> sel = results[i] ;
      if(sel.hasError()) {
        ctx.console().println(sel.getError());
      }
    }
  }
}