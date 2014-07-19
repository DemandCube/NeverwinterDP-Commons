package com.neverwinterdp.server.cluster.hazelcast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.util.text.StringUtil;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class HazelcastMemberSelector {
  private Map<String, Member> memberMap = new HashMap<String, Member>() ;
  
  public HazelcastMemberSelector(HazelcastInstance instance) {
    for(Member sel : instance.getCluster().getMembers()) {
      memberMap.put(sel.getUuid(), sel) ;
    }
  }
  
  public Member selectMember(ClusterMember cmember) {
    return memberMap.get(cmember.getUuid()) ;
  }

  public Member[] selectMember(ClusterMember[] cmember) {
    Member[] member = new Member[cmember.length] ;
    for(int i = 0; i < member.length; i++) {
      member[i] = memberMap.get(cmember[i].getUuid()) ;
    }
    return member ;
  }
  
  public List<Member> selectMemberAsList(ClusterMember[] cmember) {
    List<Member> members = new ArrayList<Member>() ;
    Member[] member = new Member[cmember.length] ;
    for(int i = 0; i < member.length; i++) {
      members.add(memberMap.get(cmember[i].getUuid())) ;
    }
    return members ;
  }
  
  public ClusterMember selectClusterMember(String connect) {
    int    port = 5700 ;
    String host = connect ;
    if(connect.indexOf(':') > 0) {
      String[] parts = StringUtil.toStringArray(connect, ":") ;
      host = parts[0] ;
      port = Integer.parseInt(parts[1]) ;
    }
    for(Member sel : this.memberMap.values()) {
      ClusterMember cmember = new ClusterMemberImpl(sel) ;
      if(host.equals(cmember.getHost()) || host.equals(cmember.getIpAddress())) {
        if(port == cmember.getPort()) return cmember ;
      }
    }
    return null ;
  }
  
  public ClusterMember selectClusterMemberByUuid(String uuid) {
    for(Member sel : this.memberMap.values()) {
      if(uuid.equals(sel.getUuid())) {
        return new ClusterMemberImpl(sel) ;
      }
    }
    return null ;
  }
}
