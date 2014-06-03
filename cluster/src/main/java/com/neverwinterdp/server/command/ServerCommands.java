package com.neverwinterdp.server.command;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.ServerRegistration;
import com.neverwinterdp.server.ServerState;
import com.neverwinterdp.util.monitor.MonitorRegistry;
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
  
  static public class GetMonitorRegistry extends ServerCommand<String> {
    static ObjectMapper mapper ;
    static {
      mapper = new ObjectMapper() ; 
      mapper.enable(SerializationFeature.INDENT_OUTPUT);
      mapper.registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.MILLISECONDS, false));
    }
    
    public String execute(Server server) throws Exception {
      MonitorRegistry registry = server.getMonitorRegistry() ;
      String json = mapper.writeValueAsString(registry) ;
      return json ;
    }
  }
}
