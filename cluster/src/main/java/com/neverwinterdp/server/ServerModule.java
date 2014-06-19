package com.neverwinterdp.server;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.server.cluster.ClusterService;
import com.neverwinterdp.server.cluster.hazelcast.HazelcastClusterService;
import com.neverwinterdp.server.module.ModuleContainer;
import com.neverwinterdp.util.LoggerFactory;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class ServerModule extends AbstractModule {
  private Map<String, String> properties ;
  
  public ServerModule() {
    properties = new HashMap<String, String>() ;
    properties.put("server.group", "NeverwinterDP") ;
    properties.put("server.cluster-framework", "hazelcast") ;
  }
  
  public ServerModule(Map<String, String> properties) {
    this.properties = properties ;
  }
  
  @Override
  protected void configure() {
    Names.bindProperties(binder(), properties) ;
    HazelcastClusterService clusterService = new HazelcastClusterService() ;
    ClusterMember member = clusterService.getMember();
    properties.put("cluster.ip-address", member.getIpAddress()) ;
    properties.put("cluster.listen-port", Integer.toString(member.getPort())) ;
    String hostId = member.toString() ;

    bind(ClusterService.class).toInstance(clusterService);
    
    ApplicationMonitor appMonitor = new ApplicationMonitor(hostId, "server") ;
    clusterService.setApplicationMonitor(appMonitor);
    bind(ApplicationMonitor.class).toInstance(appMonitor);
    
    LoggerFactory loggerFactory = new LoggerFactory("[" + hostId + "][NeverwinterDP] ") ;
    clusterService.setLoggerFactory(loggerFactory);
    bind(LoggerFactory.class).toInstance(loggerFactory);
    
    bind(ModuleContainer.class).asEagerSingleton();
    bind(Server.class).asEagerSingleton();
  }
}
