package com.neverwinterdp.server.gateway;

import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.server.cluster.ClusterRegistration;

public class ClusterPlugin extends Plugin {
  protected Object doCall(String commandName, CommandParams params) throws Exception {
    if("registration".equals(commandName)) return registration() ;
    else if("member".equals(commandName)) return members() ;
    return null ;
  }

  public ClusterMember[] members() {
    ClusterRegistration registration = clusterClient.getClusterRegistration() ;
    return registration.getMembers() ;
  }
  
  public ClusterRegistration registration() { return clusterClient.getClusterRegistration()  ; }
}