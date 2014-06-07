package com.neverwinterdp.server.client;

import com.neverwinterdp.server.ServerRegistration;
import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.server.cluster.ClusterRegistraton;
import com.neverwinterdp.server.cluster.hazelcast.HazelcastClusterClient;
import com.neverwinterdp.util.JSONSerializer;


public class Cluster {
  private ClusterClient clusterClient ;
  
  public Server server ;
  public Module module ;
  
  public Cluster() {
    connect() ;
  }
  
  public Cluster(String ... connect) {
    connect(connect) ;
  }
  
  public void connect(String ... connect)  {
    if(clusterClient != null) clusterClient.shutdown(); 
    clusterClient = new HazelcastClusterClient(connect) ;
    server = new Server(clusterClient) ;
    module = new Module(clusterClient) ;
  }
  
  public String getMembers() {
    ClusterRegistraton reg = clusterClient.getClusterRegistration() ;
    ServerRegistration[] sreg = reg.getServerRegistration() ;
    ClusterMember[] member = new ClusterMember[sreg.length] ;
    for(int i = 0; i < member.length; i++) member[i] = sreg[i].getClusterMember() ;
    return JSONSerializer.INSTANCE.toString(member) ;
  }
  
  public String clusterRegistration() {
    return  JSONSerializer.INSTANCE.toString(clusterClient.getClusterRegistration())  ;
  }
  
  public ClusterRegistraton getClusterRegistration() {
    return  clusterClient.getClusterRegistration()  ;
  }
  
  public ClusterClient getClusterClient() { return this.clusterClient ; }
  
  public void close() {
    if(clusterClient != null) clusterClient.shutdown(); 
  }
}