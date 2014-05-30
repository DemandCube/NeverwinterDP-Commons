package com.neverwinterdp.server.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.Test;

import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.ServerConfig;
import com.neverwinterdp.server.ModuleContainer;
import com.neverwinterdp.server.service.HelloService;
import com.neverwinterdp.server.service.HelloModule;

public class ServerBuilderUnitTest {
  @Test
  public void testBuilder() {
    Properties properties = new Properties() ;
    properties.put("server.group", "NeverwinterDP") ;
    properties.put("server.cluster-framework", "hazelcast") ;
    properties.put("server.roles", "master") ;
    properties.put("server.available-modules", HelloModule.class.getName()) ;
    properties.put("server.install-modules", HelloModule.class.getName()) ;
    properties.put("server.install-modules-autostart", "true") ;
    
    Server server = Server.create(properties);
    ServerConfig config = server.getConfig() ;
    assertNotNull(config) ;
    assertEquals("hazelcast", config.getClusterFramework()) ;
    
    assertNotNull(server.getMonitorRegistry()) ;
    assertNotNull(server.getLoggerFactory()) ;
    
    ModuleContainer moduleContainer = server.getModuleContainer() ;
    HelloService helloService = 
      moduleContainer.getInstance("HelloServiceModule", HelloService.class) ;
    assertNotNull(helloService) ;
    assertEquals("NeverwinterDP", helloService.getServerGroup()) ;
    assertEquals("HelloService", helloService.getServiceRegistration().getServiceId()) ;
    assertNotNull(helloService.getMonitorRegistry()) ;
    
    HelloService helloServiceInstance = 
      moduleContainer.getService("HelloServiceModule", "HelloServiceInstance") ;
    assertEquals("HelloServiceInstance", helloServiceInstance.getServiceRegistration().getServiceId()) ;
    assertNotNull(helloServiceInstance.getMonitorRegistry()) ;
  }
}
