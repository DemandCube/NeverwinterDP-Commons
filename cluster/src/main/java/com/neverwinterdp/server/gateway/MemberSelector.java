package com.neverwinterdp.server.gateway;

import java.io.Serializable;
import java.util.Random;

import com.beust.jcommander.Parameter;
import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.server.command.ServerCommand;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.command.ServiceCommand;
import com.neverwinterdp.server.command.ServiceCommandResult;

public class MemberSelector implements Serializable {
  @Parameter(names = {"--member-uuid"}, description = "Select the member by uuid")
  public String memberUuid ;
  
  @Parameter(names = {"--member-ip-port"}, description = "Select the member by ip port")
  public String ipPort ;
  
  @Parameter(names = {"--member-role"}, description = "Select the member by role")
  public String memberRole ;
  
  @Parameter(names = {"--member-name"}, description = "Select the member by name")
  public String memberName ;
  
  @Parameter(names = {"--timeout", "--member-wait"}, description = "Command timeout")
  public long timeout = 10000 ;
  
  
  public MemberSelector() {} 
  
  public ClusterMember[] getMembers(ClusterClient clusterClient) {
    if(memberUuid != null) {
      ClusterMember member = clusterClient.getClusterMemberByUuid(memberUuid) ;
      if(member == null) return new ClusterMember[0] ;
      return new ClusterMember[] { member } ;
    } else if(ipPort != null) {
      ClusterMember member = clusterClient.getClusterMember(ipPort) ;
      if(member == null) return new ClusterMember[0] ;
      return new ClusterMember[] { member } ;
    } else if(memberName != null) {
      return clusterClient.getClusterRegistration().findClusterMemberByName(memberName) ;
    } else if(memberRole != null) {
      return clusterClient.getClusterRegistration().findClusterMemberByRole(memberRole) ;
    }
    return null ;
  }
  
  public ClusterMember selectRandomMember(ClusterClient clusterClient) {
    ClusterMember[] members = getMembers(clusterClient) ;
    if(members == null || members.length == 0) {
      throw new RuntimeException("Expect at least 1 member") ;
    }
    return members[new Random().nextInt(members.length)] ;
  }
  
  public <T> ServerCommandResult<T>[] execute(ClusterClient client, ServerCommand<T> command) {
    ClusterMember[] members = getMembers(client) ;
    command.setTimeout(timeout) ;
    if(members == null) return client.execute(command) ; 
    else return client.execute(command, members) ;
  }
  
  public <T> ServiceCommandResult<T>[] execute(ClusterClient client, ServiceCommand<T> command) {
    ClusterMember[] members = getMembers(client) ;
    command.setTimeout(timeout) ;
    if(members == null) return client.execute(command) ; 
    else return client.execute(command, members) ;
  }
}