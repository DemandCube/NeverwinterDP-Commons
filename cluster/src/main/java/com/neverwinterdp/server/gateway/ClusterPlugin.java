package com.neverwinterdp.server.gateway;

import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.cluster.ClusterRegistration;

public class ClusterPlugin extends CommandPlugin {
  public ClusterPlugin() {
    add("member", new member()) ;
    add("registration", new registration()) ;
  }
  
  static public class member implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ClusterRegistration registration = clusterClient.getClusterRegistration() ;
      return registration.getMembers() ;
    }
  }
  
  static public class registration implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      return clusterClient.getClusterRegistration() ;
    }
  }
}