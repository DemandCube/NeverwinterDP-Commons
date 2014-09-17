package com.neverwinterdp.server.command;

import com.beust.jcommander.Parameter;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.ServerRegistration;
import com.neverwinterdp.server.ServerState;
import com.neverwinterdp.util.jvm.JVMInfo;
import com.neverwinterdp.yara.MetricRegistry;
import com.neverwinterdp.yara.snapshot.MetricRegistrySnapshot;
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
  
  static public class GetMetricRegistry extends ServerCommand<MetricRegistry> {
    @Parameter(names = {"--filter"}, description = "Filter by the pattern expression")
    private String filter ;
    
    public GetMetricRegistry() { }
    
    public GetMetricRegistry(String filter) {
      this.filter = filter ;
    }
    
    public MetricRegistry execute(Server server) throws Exception {
      MetricRegistry registry = server.getMetricRegistry() ;
      return registry ;
    }
  }
  
  static public class GetMetricRegistrySnapshot extends ServerCommand<MetricRegistrySnapshot> {
    public GetMetricRegistrySnapshot() { }
    
    public MetricRegistrySnapshot execute(Server server) throws Exception {
      MetricRegistry registry = server.getMetricRegistry() ;
      return new MetricRegistrySnapshot(registry) ;
    }
  }
  
  static public class ClearMetricRegistry extends ServerCommand<Integer> {
    @Parameter(names = {"--expression"}, description = "Clear the metric with the name pattern")
    private String nameExp ;
    
    public ClearMetricRegistry() { }
    
    public ClearMetricRegistry(String nameExp) {
      this.nameExp = nameExp ;
    }
    
    public Integer execute(Server server) throws Exception {
      MetricRegistry registry = server.getMetricRegistry() ;
      return registry.remove(nameExp) ;
    }
  }
  
  static public class GetJVMInfo extends ServerCommand<JVMInfo> {
    public JVMInfo execute(Server server) throws Exception {
      return new JVMInfo() ;
    }
  }
}