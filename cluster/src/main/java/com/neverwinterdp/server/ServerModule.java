package com.neverwinterdp.server;

import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.neverwinterdp.server.cluster.ClusterService;
import com.neverwinterdp.server.cluster.hazelcast.HazelcastClusterService;
import com.neverwinterdp.server.service.HelloServiceContainerModule;
import com.neverwinterdp.util.LoggerFactory;
import com.neverwinterdp.util.monitor.MonitorRegistry;

public class ServerModule extends AbstractModule {
  private Properties properties ;
  
  public ServerModule() {
    properties = new Properties() ;
    properties.put("server.group", "NeverwinterDP") ;
    properties.put("server.cluster-framework", "hazelcast") ;
    properties.put("server.roles", "master") ;
    properties.put("server.service-container-module", HelloServiceContainerModule.class.getName()) ;
  }
  
  public ServerModule(Properties properties) {
    this.properties = properties ;
  }
  
  @Override
  protected void configure() {
    Names.bindProperties(binder(), properties) ;
    
    ClusterService clusterService = new HazelcastClusterService() ;
    String hostId = clusterService.getMember().toString() ;
    bind(ClusterService.class).toInstance(clusterService);
    
    MonitorRegistry monitorRegistry = new MonitorRegistry(hostId, "server") ;
    bind(MonitorRegistry.class).toInstance(monitorRegistry);
    
    LoggerFactory loggerFactory = new LoggerFactory("[" + hostId + "][NeverwinterDP] ") ;
    bind(LoggerFactory.class).toInstance(loggerFactory);

    bind(ServiceContainer.class).asEagerSingleton();
    bind(Server.class).asEagerSingleton();
  }
}
