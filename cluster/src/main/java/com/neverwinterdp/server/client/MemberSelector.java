package com.neverwinterdp.server.client;

import com.beust.jcommander.Parameter;
import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.server.command.ServerCommand;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.command.ServiceCommand;
import com.neverwinterdp.server.command.ServiceCommandResult;

public class MemberSelector {
  @Parameter(names = {"--member"}, description = "Select the member by host:port")
  String member ;
  
  @Parameter(names = {"--member-role"}, description = "Select the member by role")
  String memberRole ;
  
  public MemberSelector() {} 
  
  public MemberSelector(CommandParams params) {
    this.member = params.getString("member") ;
    this.memberRole = params.getString("member-role") ;
  }
  
  public ClusterMember[] getMembers(ClusterClient clusterClient) {
    if(member != null) {
      return new ClusterMember[] { clusterClient.getClusterMember(member)} ;
    } else if(memberRole != null) {
      return clusterClient.getClusterRegistration().findClusterMemberByRole(memberRole) ;
    }
    return null ;
  }
  
  public <T> ServerCommandResult<T>[] execute(ClusterClient client, ServerCommand<T> command) {
    ClusterMember[] members = getMembers(client) ;
    if(members == null) return client.execute(command) ; 
    else return client.execute(command, members) ;
  }
  
  public <T> ServiceCommandResult<T>[] execute(ClusterClient client, ServiceCommand<T> command) {
    ClusterMember[] members = getMembers(client) ;
    if(members == null) return client.execute(command) ; 
    else return client.execute(command, members) ;
  }
}