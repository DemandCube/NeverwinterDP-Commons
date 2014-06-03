package com.neverwinterdp.server.shell;

import com.beust.jcommander.Parameter;
import com.neverwinterdp.server.cluster.ClusterMember;

public class MemberSelectorOption {
  @Parameter(names = {"--member"}, description = "Select the member by host:port")
  String member ;
  
  @Parameter(names = {"--member-role"}, description = "Select the member by role")
  String memberRole ;
  
  public ClusterMember[] getMembers(ShellContext ctx) {
    if(member != null) {
      return new ClusterMember[] {ctx.getClusterClient().getClusterMember(member)} ;
    } else if(memberRole != null) {
      return ctx.getClusterClient().getClusterRegistration().findClusterMemberByRole(memberRole) ;
    }
    return null ;
  }
}