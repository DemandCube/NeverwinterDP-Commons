package com.neverwinterdp.server.cluster.shell;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.neverwinterdp.server.ModuleStatus;
import com.neverwinterdp.server.ModuleStatus.InstallStatus;
import com.neverwinterdp.server.ModuleStatus.RunningStatus;
import com.neverwinterdp.server.ServerRegistration;
import com.neverwinterdp.server.ServerState;
import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.server.command.ServerCommand;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.command.ServerCommands;
import com.neverwinterdp.server.command.ServerModuleCommands;
import com.neverwinterdp.server.service.ServiceRegistration;
import com.neverwinterdp.server.service.ServiceState;
import com.neverwinterdp.util.text.TabularPrinter;

public class ClusterCommandGroup extends CommandGroup {
  public ClusterCommandGroup() {
    super("cluster") ;
    add("ping", Ping.class);
    add("connect", Connect.class);
    add("registration", Registration.class);
    add("module", Module.class);
  }
  
  static public class Ping extends Command {
    @Parameter(names = {"-m","--member"}, description = "member in the host:port format")
    String member = null ;
    
    public void execute(ShellContext ctx) {
      ClusterClient client = ctx.getClusterClient() ;
      ServerCommand<ServerState> ping = new ServerCommands.Ping() ;
      ServerCommandResult<ServerState>[] results = null ;
      if(member == null) {
        results = client.execute(ping) ; 
      } else {
        ClusterMember cmember = client.getClusterMember(member) ;
        results = new ServerCommandResult[] { client.execute(ping, cmember) };
      }
      TabularPrinter tprinter = ctx.console().tabularPrinter(30, 10, 10) ;
      tprinter.header("Host IP", "Port", "State");
      for(int i = 0; i < results.length; i++) {
        ServerCommandResult<ServerState> sel = results[i] ;
        String host = sel.getFromMember().getIpAddress() ;
        int    port = sel.getFromMember().getPort() ;
        ServerState state = sel.getResult() ;
        tprinter.row(host, port, state);
      }
    }
  }
  
  @Parameters(commandDescription = "Connect to a cluster")
  static public class Connect extends Command {
    @Parameter(names = {"-m", "--members"}, variableArity = true, description = "the member list in host:port format")
    List<String> members  = new ArrayList<String>();
    
    public void execute(ShellContext ctx) {
      ctx.connect(members.toArray(new String[members.size()])) ;
      ctx.console().println("Connect Successfully to " + members);
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
  
  @Parameters(commandDescription = "execute various module option such list, install, uninstall")
  static public class Module extends Command {
    @Parameter(names = {"--member"}, description = "Select the member by host:port")
    String member ;
    
    @Parameter(names = {"--member-role"}, description = "Select the member by role")
    String memberRole ;
    
    @Parameter(names = {"--available"}, description = "List the available modules")
    boolean available ;
    
    @Parameter(names = {"--installed"}, description = "List the installed modules")
    boolean installed ;
    
    @Parameter(names = {"--install"}, description = "List of module to install")
    List<String> installModules = new ArrayList<String>() ;
    
    @Parameter(names = {"--install-autostart"}, description = "Autostart after install")
    boolean installAutostart = false ;
    
    @Parameter(names = {"--uninstall"}, description = "List of module to uninstall")
    List<String> uninstallModules = new ArrayList<String>() ;
    
    
    public void execute(ShellContext ctx) {
      if(installModules.size() > 0) {
        ServerCommand<ModuleStatus[]> installCommand = 
            new ServerModuleCommands.InstallModule(installModules, installAutostart) ;
        list(ctx, installCommand, "Install") ;
      }
      if(uninstallModules.size() > 0) {
        ServerCommand<ModuleStatus[]> uninstallCommand = 
            new ServerModuleCommands.UninstallModule(uninstallModules) ;
        list(ctx, uninstallCommand, "Uninstall") ;
      }
      
      if(available) {
        list(ctx, new ServerModuleCommands.GetAvailableModule(), "List available") ;
      }
      if(installed) {
        list(ctx, new ServerModuleCommands.GetInstallModule(), "List installed") ;
      }
    }
    
    private void list(ShellContext ctx, ServerCommand<ModuleStatus[]> command, String title) {
      ClusterClient client = ctx.getClusterClient() ;
      ServerCommandResult<ModuleStatus[]>[] results = null ;
      if(command != null) {
        results = client.execute(command) ; 
      } else {
        ClusterMember cmember = client.getClusterMember(member) ;
        results = new ServerCommandResult[] { client.execute(command, cmember) };
      }
      
      for(int i = 0; i < results.length; i++) {
        ServerCommandResult<ModuleStatus[]> sel = results[i] ;
        TabularPrinter printer = ctx.console().tabularPrinter(30, 10, 10) ;
        ctx.console().header(title + " on member " + sel.getFromMember());
        printer.setIndent("  ") ;
        printer.header("Module", "Install", "Status");
        ModuleStatus[] mstatus = sel.getResult() ;
        for(ModuleStatus selStatus : mstatus) {
          String moduleName = selStatus.getModuleName() ;
          InstallStatus installStatus = selStatus.getInstallStatus() ;
          RunningStatus runningStatus = selStatus.getRunningStatus() ;
          printer.row(moduleName, installStatus, runningStatus);
        }
      }
    }
  }
}