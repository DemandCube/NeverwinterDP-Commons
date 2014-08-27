package com.neverwinterdp.server.shell;

import java.util.List;
import java.util.Map;

import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.neverwinterdp.server.ServerRegistration;
import com.neverwinterdp.server.ServerState;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.gateway.Command;
import com.neverwinterdp.server.gateway.MemberSelector;
import com.neverwinterdp.server.service.ServiceRegistration;
import com.neverwinterdp.server.service.ServiceState;
import com.neverwinterdp.util.monitor.snapshot.ApplicationMonitorSnapshot;
import com.neverwinterdp.util.monitor.snapshot.MetricFormater;
import com.neverwinterdp.util.monitor.snapshot.TimerSnapshot;
import com.neverwinterdp.util.text.TabularPrinter;

@ShellCommandConfig(name = "server")
public class ShellServerCommand extends ShellCommand {
  public ShellServerCommand() {
    super("cluster") ;
    add("ping", Ping.class);
    add("shutdown", Shutdown.class);
    add("start", Start.class);
    add("exit", Exit.class);
    add("registration", Registration.class);
    add("metric", Metric.class);
    add("metric-clear", MetricClear.class);
  }
  
  static public class Ping extends ShellSubCommand {
    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      ServerCommandResult<ServerState>[] results = ctx.getClusterGateway().execute(command) ;
      String[] explaination = {
        "The result is the running state of the server."
      };
      CommandResultPrinterUtil.printPrimitiveServerResults(ctx, command, results, explaination);
    }
  }
  
  @Parameters(commandDescription = "List the server registration and service registration of each member")
  static public class Registration extends ShellSubCommand {
    public void execute(Shell shell, ShellContext ctx, Command command) {
      ServerRegistration[] registration = ctx.getClusterGateway().getClusterRegistration().getServerRegistration() ;
      Console console = ctx.console() ;
      String indent = "  " ;
      for(ServerRegistration server : registration) {
        console.header("Member " + server.getClusterMember().toString());
        TabularPrinter iprinter = console.tabularPrinter(20, 30) ;
        iprinter.setIndent(indent) ;
        iprinter.row("UUID:", server.getClusterMember().getUuid());
        iprinter.row("Host:", server.getClusterMember().getHost());
        iprinter.row("IP Address:", server.getClusterMember().getIpAddress());
        iprinter.row("Port:", server.getClusterMember().getPort());
        iprinter.row("State", server.getServerState());
        iprinter.row("Roles", server.getRoles());
        List<ServiceRegistration> services = server.getServices() ;
        TabularPrinter sprinter = console.tabularPrinter(20, 30, 10) ;
        sprinter.setIndent(indent);
        console.println();
        console.println(indent, "Service Registration");
        sprinter.header("Module", "Service", "State");
        for(ServiceRegistration service : services) {
          String module = service.getModule() ;
          String serviceId = service.getServiceId() ;
          ServiceState state = service.getState() ;
          sprinter.row(module, serviceId, state);
        }
      }
    }
  }
  
  static public class Shutdown extends ShellSubCommand {
    @ParametersDelegate
    MemberSelector memberSelector = new MemberSelector();
    
    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      ServerCommandResult<ServerState>[] results = ctx.getClusterGateway().execute(command) ;
      String[] explaination = {
        "The result is the running state of the server."
      };
      CommandResultPrinterUtil.printPrimitiveServerResults(ctx, command, results, explaination);
    }
  }

  static public class Start extends ShellSubCommand {
    @ParametersDelegate
    MemberSelector memberSelector = new MemberSelector();
    
    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      ServerCommandResult<ServerState>[] results = ctx.getClusterGateway().execute(command) ;
      String[] explaination = {
        "The result is the running state of the server."
      };
      CommandResultPrinterUtil.printPrimitiveServerResults(ctx, command, results, explaination);
    }
  }

  static public class Exit extends ShellSubCommand {
    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      ServerCommandResult<ServerState>[] results = ctx.getClusterGateway().execute(command) ;
      String[] explaination = {
        "The result is the running state of the server."
      };
      CommandResultPrinterUtil.printPrimitiveServerResults(ctx, command, results, explaination);
    }
  }
  
  static public class Metric extends ShellSubCommand {
    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      ServerCommandResult<ApplicationMonitorSnapshot>[] results = ctx.getClusterGateway().execute(command) ;
      MetricFormater formater = new MetricFormater("  ") ;
      ctx.console().header(command.getCommandLine());
      for(ServerCommandResult<ApplicationMonitorSnapshot> sel : results) {
        ApplicationMonitorSnapshot snapshot = sel.getResult() ;
        ctx.console().println(sel.getFromMember().toString() + " - member name " + sel.getFromMember().getMemberName());
        Map<String, TimerSnapshot> timers = snapshot.getRegistry().getTimers() ;
        ctx.console().println(formater.format(timers));
      }
    }
  }
  
  static public class MetricClear extends ShellSubCommand {
    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      ServerCommandResult<Integer>[] results = ctx.getClusterGateway().execute(command) ;
      String[] explaination = {
        "The result is the number of the removed metric."
      };
      CommandResultPrinterUtil.printPrimitiveServerResults(ctx, command, results, explaination);
        
    }
  }
}
