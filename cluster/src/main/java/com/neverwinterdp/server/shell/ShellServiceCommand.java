package com.neverwinterdp.server.shell;

import com.neverwinterdp.server.command.ServiceCommandResult;
import com.neverwinterdp.server.gateway.Command;

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
      String[] explaination = {
        "The result is true when the service implement the cleanup method. "
      };
      CommandResultPrinterUtil.printPrimitiveServiceResults(ctx, command, results, explaination);
    }
  }
  
  static public class Start extends ShellSubCommand {
    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      ServiceCommandResult<Boolean>[] results = ctx.getClusterGateway().execute(command) ;
      String[] explaination = {
        "The result is true when the service succesfully start. "
      };
      CommandResultPrinterUtil.printPrimitiveServiceResults(ctx, command, results, explaination);
    }
  }
  
  static public class Stop extends ShellSubCommand {
    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      ServiceCommandResult<Boolean>[] results = ctx.getClusterGateway().execute(command) ;
      String[] explaination = {
        "The result is true when the service succesfully stop."
      };
      CommandResultPrinterUtil.printPrimitiveServiceResults(ctx, command, results, explaination);
    }
  }
  
  static public class Restart extends ShellSubCommand {
    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      ServiceCommandResult<Boolean>[] results = ctx.getClusterGateway().execute(command) ;
      String[] explaination = {
        "The result is true when the service succesfully restart. "
      };
      CommandResultPrinterUtil.printPrimitiveServiceResults(ctx, command, results, explaination);
    }
  }
  
  static public class Configure extends ShellSubCommand {
    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      ServiceCommandResult<Boolean>[] results = ctx.getClusterGateway().execute(command) ;
      String[] explaination = {
        "The result is true when the service succesfully reconfigure with the new properties. "
      };
      CommandResultPrinterUtil.printPrimitiveServiceResults(ctx, command, results, explaination);
    }
  }
}