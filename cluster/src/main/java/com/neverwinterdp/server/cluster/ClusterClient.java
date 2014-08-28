package com.neverwinterdp.server.cluster;

import com.neverwinterdp.server.ServerRegistration;
import com.neverwinterdp.server.command.ServerCommand;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.command.ServiceCommand;
import com.neverwinterdp.server.command.ServiceCommandResult;

/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public interface ClusterClient {
  public ClusterRegistration getClusterRegistration() ;
  public void addListener(ClusterListener<ClusterClient> listener) ;
  public void removeListener(ClusterListener<ClusterClient> listener) ;
  
  public void broadcast(ClusterEvent event) ; 

  public ClusterMember getClusterMember(String connect) ;
  
  public ClusterMember getClusterMemberByUuid(String uuid) ;
  
  public ClusterMember[] findClusterMemberByName(String nameExp) ;
  
  public ClusterMember[] findClusterMemberByRole(String roleExp) ;
  
  public ServerRegistration getServerRegistration(ClusterMember member) ; 
  
  public <T> ServiceCommandResult<T> execute(ServiceCommand<T> command, ClusterMember member) ;
  
  public <T> ServiceCommandResult<T>[] execute(ServiceCommand<T> command, ClusterMember[] member) ;
  
  public <T> ServiceCommandResult<T>[] execute(ServiceCommand<T> command) ;
  
  public <T> ServerCommandResult<T> execute(ServerCommand<T> command, ClusterMember member) ;
  
  public <T> ServerCommandResult<T>[] execute(ServerCommand<T> command, ClusterMember[] member) ;
  
  public <T> ServerCommandResult<T>[] execute(ServerCommand<T> command) ;
  
  public void shutdown() ;
}