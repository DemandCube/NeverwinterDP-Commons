package com.neverwinterdp.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.Test;

import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.ServerConfig;
import com.neverwinterdp.server.ServiceContainer;
import com.neverwinterdp.server.service.HelloService;
import com.neverwinterdp.server.service.HelloServiceModule;

public class ServerBuilderUnitTest {
  @Test
  public void testBuilder() {
    Properties properties = new Properties() ;
    properties.put("server.group", "NeverwinterDP") ;
    properties.put("server.cluster-framework", "hazelcast") ;
    properties.put("server.roles", "master") ;
    properties.put("server.service-module", HelloServiceModule.class.getName()) ;
    
    Server server = Server.create(properties);
    ServerConfig config = server.getConfig() ;
    assertNotNull(config) ;
    assertEquals("hazelcast", config.getClusterFramework()) ;
    
    assertNotNull(server.getMonitorRegistry()) ;
    assertNotNull(server.getLoggerFactory()) ;
    
    ServiceContainer serviceContainer = server.getServiceContainer() ;
    HelloService helloService = serviceContainer.getInstance(HelloService.class) ;
    assertNotNull(helloService) ;
    assertEquals("NeverwinterDP", helloService.getServerGroup()) ;
    assertEquals("HelloService", helloService.getServiceId()) ;
    assertNotNull(helloService.getMonitorRegistry()) ;
    
    HelloService helloServiceInstance = serviceContainer.getService("HelloServiceInstance") ;
    assertEquals("HelloServiceInstance", helloServiceInstance.getServiceId()) ;
    assertNotNull(helloServiceInstance.getMonitorRegistry()) ;
  }
}
