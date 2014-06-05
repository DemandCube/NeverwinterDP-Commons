package com.neverwinterdp.server.shell;

import java.util.List;
import java.util.Map;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.neverwinterdp.server.ServerRegistration;
import com.neverwinterdp.server.ServerState;
import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.server.command.ServerCommand;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.command.ServerCommands;
import com.neverwinterdp.server.service.ServiceRegistration;
import com.neverwinterdp.server.service.ServiceState;
import com.neverwinterdp.util.monitor.snapshot.ApplicationMonitorSnapshot;
import com.neverwinterdp.util.monitor.snapshot.MetricFormater;
import com.neverwinterdp.util.monitor.snapshot.TimerSnapshot;
import com.neverwinterdp.util.text.TabularPrinter;

@CommandGroupConfig(name = "server")
public class ServerCommandGroup extends CommandGroup {
  public ServerCommandGroup() {
    super("cluster") ;
    add("ping", Ping.class);
    add("shutdown", Shutdown.class);
    add("exit", Exit.class);
    add("registration", Registration.class);
    add("metric", Metric.class);
  }
  
  static public class Ping extends Command {
    @ParametersDelegate
    MemberSelectorOption memberSelector = new MemberSelectorOption();
    
    public void execute(ShellContext ctx) {
      ClusterClient client = ctx.getClusterClient() ;
      ServerCommand<ServerState> ping = new ServerCommands.Ping() ;
      ClusterMember[] members = memberSelector.getMembers(ctx) ;
      ServerCommandResult<ServerState>[] results = null ;
      if(members == null) {
        results = client.execute(ping) ; 
      } else {
        results = client.execute(ping, members) ;
      }
      printServerStateResults(ctx, results) ;
    }
  }
  
  @Parameters(commandDescription = "List the server registration and service registration of each member")
  static public class Registration extends Command {
    @Parameter(names = {"-f", "--filter"}, description = "filter expression to match host or ip")
    String filter;
    
    public void execute(ShellContext ctx) {
      ServerRegistration[] registration = 
          ctx.getClusterClient().getClusterRegistration().getServerRegistration() ;
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
  
  static public class Shutdown extends Command {
    @ParametersDelegate
    MemberSelectorOption memberSelector = new MemberSelectorOption();
    
    public void execute(ShellContext ctx) {
      ClusterClient client = ctx.getClusterClient() ;
      ServerCommand<ServerState> shutdown = new ServerCommands.Shutdown() ;
      ServerCommandResult<ServerState>[] results = null ;
      ClusterMember[] members = memberSelector.getMembers(ctx) ;
      if(members == null) {
        results = client.execute(shutdown) ; 
      } else {
        results = client.execute(shutdown, members) ;
      }
      printServerStateResults(ctx, results) ;
    }
  }

  static public class Exit extends Command {
    @Parameter(names = {"-t","--wait-time"}, description = "Wait time before shutdown")
    long waitTime = 3000 ;
    
    @ParametersDelegate
    MemberSelectorOption memberSelector = new MemberSelectorOption();
    
    public void execute(ShellContext ctx) {
      ClusterClient client = ctx.getClusterClient() ;
      ServerCommand<ServerState> shutdown = new ServerCommands.Exit(waitTime) ;
      ServerCommandResult<ServerState>[] results = null ;
      ClusterMember[] members = memberSelector.getMembers(ctx) ;
      if(members == null) {
        results = client.execute(shutdown) ; 
      } else {
        results = client.execute(shutdown, members) ;
      }
      printServerStateResults(ctx, results) ;
    }
  }
  
  static public class Metric extends Command {
    @ParametersDelegate
    MemberSelectorOption memberSelector = new MemberSelectorOption();
    
    @Parameter(names = {"-t","--type"}, description = "Metric type: counter, timer, meter, histogram")
    String type = "timer" ;
    
    @Parameter(names = {"-f","--filter"}, description = "filter by regrex syntax")
    String filter = "*" ;
    
    
    public void execute(ShellContext ctx) {
      ClusterClient client = ctx.getClusterClient() ;
      ServerCommand<ApplicationMonitorSnapshot> getCommand = new ServerCommands.GetMonitorSnapshot() ;
      ServerCommandResult<ApplicationMonitorSnapshot>[] results = null ;
      ClusterMember[] members = memberSelector.getMembers(ctx) ;
      if(members == null) {
        results = client.execute(getCommand) ; 
      } else {
        results = client.execute(getCommand, members) ;
      }
      if("timer".equals(type)) {
        MetricFormater formater = new MetricFormater("  ") ;
        for(ServerCommandResult<ApplicationMonitorSnapshot> sel : results) {
          ApplicationMonitorSnapshot snapshot = sel.getResult() ;
          ctx.console().header(sel.getFromMember().toString());
          Map<String, TimerSnapshot> timers = snapshot.getRegistry().findTimers(filter) ;
          ctx.console().println(formater.format(timers));
        }
      } else {
        
      }
    }
  }
  
  static void printServerStateResults(ShellContext ctx, ServerCommandResult<ServerState>[] results) {
    TabularPrinter tprinter = ctx.console().tabularPrinter(30, 10, 10) ;
    tprinter.header("Host IP", "Port", "State");
    for(int i = 0; i < results.length; i++) {
      ServerCommandResult<ServerState> sel = results[i] ;
      String host = sel.getFromMember().getIpAddress() ;
      int    port = sel.getFromMember().getPort() ;
      ServerState state = sel.getResult() ;
      tprinter.row(host, port, state);
    }
    
    for(int i = 0; i < results.length; i++) {
      ServerCommandResult<ServerState> sel = results[i] ;
      if(sel.hasError()) {
        ctx.console().println(sel.getError());
      }
    }
  }
}