package com.neverwinterdp.server;

import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.neverwinterdp.server.cluster.ClusterService;
import com.neverwinterdp.server.cluster.hazelcast.HazelcastClusterService;
import com.neverwinterdp.server.service.HelloServiceModule;
import com.neverwinterdp.util.LoggerFactory;
import com.neverwinterdp.util.monitor.MonitorRegistry;

public class ServerModule extends AbstractModule {
  private Properties properties ;
  
  public ServerModule() {
    properties = new Properties() ;
    properties.put("server.group", "NeverwinterDP") ;
    properties.put("server.cluster-framework", "hazelcast") ;
    properties.put("server.roles", "master") ;
    properties.put("server.service-container-module", HelloServiceModule.class.getName()) ;
  }
  
  public ServerModule(Properties properties) {
    this.properties = properties ;
  }
  
  @Override
  protected void configure() {
    Names.bindProperties(binder(), properties) ;
    
    HazelcastClusterService clusterService = new HazelcastClusterService() ;
    String hostId = clusterService.getMember().toString() ;
    bind(ClusterService.class).toInstance(clusterService);
    
    MonitorRegistry monitorRegistry = new MonitorRegistry(hostId, "server") ;
    clusterService.setMonitorRegistry(monitorRegistry);
    bind(MonitorRegistry.class).toInstance(monitorRegistry);
    
    LoggerFactory loggerFactory = new LoggerFactory("[" + hostId + "][NeverwinterDP] ") ;
    clusterService.setLoggerFactory(loggerFactory);
    bind(LoggerFactory.class).toInstance(loggerFactory);
    
    bind(RuntimeEnvironment.class).toInstance(new RuntimeEnvironment(null));
    
    bind(ServiceContainer.class).asEagerSingleton();
    bind(Server.class).asEagerSingleton();
  }
}
