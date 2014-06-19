package com.neverwinterdp.server.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.neverwinterdp.server.ServerRegistration;
import com.neverwinterdp.server.service.ServiceRegistration;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
abstract public class ClusterRegistraton {
  abstract public ServerRegistration   getServerRegistration(ClusterMember member) ;
  abstract public ServerRegistration[] getServerRegistration() ;
  abstract public void update(ServerRegistration registration) ;
  abstract public void remove(ClusterMember member) ;
  abstract public int  getNumberOfServers() ;
  
  public Map<ClusterMember, ServiceRegistration> findByServiceId(String module, String serviceId) {
    Map<ClusterMember, ServiceRegistration> map = new HashMap<ClusterMember, ServiceRegistration>() ;
    for(ServerRegistration sel : getServerRegistration()) {
      ServiceRegistration registration = sel.findByServiceId(module, serviceId) ;
      if(registration != null) {
        map.put(sel.getClusterMember(), registration) ;
      }
    }
    return map ;
  }
  
  public Map<ClusterMember, ServiceRegistration> findByClass(Class<?> type) {
    Map<ClusterMember, ServiceRegistration> map = new HashMap<ClusterMember, ServiceRegistration>() ;
    for(ServerRegistration sel : getServerRegistration()) {
      ServiceRegistration registration = sel.findByClass(type) ;
      if(registration != null) {
        map.put(sel.getClusterMember(), registration) ;
      }
    }
    return map ;
  }
  
  public ClusterMember[] findClusterMemberByRole(String role) {
    List<ClusterMember> holder = new ArrayList<ClusterMember>() ;
    for(ServerRegistration sel : getServerRegistration()) {
      if(sel.getRoles().contains(role)) {
        holder.add(sel.getClusterMember()) ;
      }
    }
    return holder.toArray(new ClusterMember[holder.size()]);
  }
}