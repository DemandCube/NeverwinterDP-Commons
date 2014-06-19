package com.neverwinterdp.server;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.server.service.ServiceRegistration;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class ServerRegistration implements Serializable {
  private ClusterMember             clusterMember;
  private Set<String> roles = new HashSet<String>() ;
  private ServerState               serverState;
  private List<ServiceRegistration> services;

  public ServerRegistration() {
  }

  public ServerRegistration(ServerConfig config, ClusterMember member, 
                            ServerState state, List<ServiceRegistration> services) {
    this.clusterMember = member ;
    this.serverState = state;
    this.services = services;
    for(String sel : config.getRoles()) {
      this.roles.add(sel) ;
    }
  }

  public ClusterMember getClusterMember() { return this.clusterMember ; }
  
  public Set<String> getRoles() { return roles ; }
  
  public ServerState getServerState() {
    return this.serverState;
  }

  public List<ServiceRegistration> getServices() {
    return services;
  }
  
  public ServiceRegistration findByServiceId(String module, String id) {
    for(int i = 0; i < services.size(); i++) {
      ServiceRegistration registration = services.get(i) ;
      if(module.equals(registration.getModule()) && 
        id.equals(registration.getServiceId())) return registration ;
    }
    return null ;
  }
  
  public ServiceRegistration findByClass(Class<?> type) {
    for(int i = 0; i < services.size(); i++) {
      ServiceRegistration registration = services.get(i) ;
      if(type.getName().equals(registration.getClassName())) return registration ;
    }
    return null ;
  }
}
