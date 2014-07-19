package com.neverwinterdp.server.command;

import com.beust.jcommander.Parameter;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.ServerRegistration;
import com.neverwinterdp.server.ServerState;
import com.neverwinterdp.util.jvm.JVMInfo;
import com.neverwinterdp.util.monitor.ApplicationMonitor;
import com.neverwinterdp.util.monitor.snapshot.ApplicationMonitorSnapshot;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class ServerCommands {
  static public class Ping extends ServerCommand<ServerState> {
    public ServerState execute(Server server) throws Exception {
      return server.getServerState() ;
    }
  }

  static public class Start extends ServerCommand<ServerState> {
    public ServerState execute(Server server) throws Exception {
      server.start() ; 
      return server.getServerState() ;
    }
  }
  
  static public class Shutdown extends ServerCommand<ServerState> {
    public ServerState execute(Server server) throws Exception {
      server.shutdown() ; 
      return server.getServerState() ;
    }
  }
  
  static public class Exit extends ServerCommand<ServerState> {
    private long waitTime = 3000 ;
    
    public Exit() {}
    
    public Exit(long time) {
      this.waitTime = time ;
    }
    
    public ServerState execute(Server server) throws Exception {
      server.exit(waitTime) ; 
      return server.getServerState() ;
    }
  }
  
  static public class GetServerRegistration extends ServerCommand<ServerRegistration> {
    public ServerRegistration execute(Server server) throws Exception {
      return server.getServerRegistration() ;
    }
  }
  
  static public class GetMonitorSnapshot extends ServerCommand<ApplicationMonitorSnapshot> {
    @Parameter(names = {"--filter"}, description = "Filter by the pattern expression")
    private String filter ;
    
    public GetMonitorSnapshot() { }
    
    public GetMonitorSnapshot(String filter) {
      this.filter = filter ;
    }
    
    public ApplicationMonitorSnapshot execute(Server server) throws Exception {
      ApplicationMonitor appMonitor = server.getApplicationMonitor() ;
      ApplicationMonitorSnapshot snapshot = appMonitor.snapshot() ;
      snapshot.filter(filter);
      return snapshot ;
    }
  }
  
  static public class ClearMonitor extends ServerCommand<Integer> {
    @Parameter(names = {"--expression"}, description = "Clear the metric with the name pattern")
    private String nameExp ;
    
    public ClearMonitor() { }
    
    public ClearMonitor(String nameExp) {
      this.nameExp = nameExp ;
    }
    
    public Integer execute(Server server) throws Exception {
      ApplicationMonitor appMonitor = server.getApplicationMonitor() ;
      return appMonitor.remove(nameExp) ;
    }
  }
  
  static public class GetJVMInfo extends ServerCommand<JVMInfo> {
    public JVMInfo execute(Server server) throws Exception {
      return new JVMInfo() ;
    }
  }
}