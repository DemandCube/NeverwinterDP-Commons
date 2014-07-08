package com.neverwinterdp.server.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.ServerConfig;
import com.neverwinterdp.server.ServerRegistration;
import com.neverwinterdp.server.cluster.hazelcast.HazelcastClusterClient;
import com.neverwinterdp.server.module.HelloModule;
import com.neverwinterdp.server.module.ModuleContainer;
import com.neverwinterdp.server.service.HelloService;

public class ServerBuilderUnitTest {
  @Test
  public void testBuilder() throws Exception {
    String[] args = {
      "-Pserver.group=NeverwinterDP", "-Pserver.name=test", "-Pserver.roles=master"
    };
    Server server = Server.create(args);
    Thread.sleep(1000);
    System.out.println("----------------------------------------");
    ServerConfig config = server.getConfig() ;
    assertNotNull(config) ;
    assertEquals("test", config.getServerName()) ;
    
    assertNotNull(server.getApplicationMonitor()) ;
    assertNotNull(server.getLoggerFactory()) ;
    
    ModuleContainer moduleContainer = server.getModuleContainer() ;
    HelloService helloService = 
      moduleContainer.getService("HelloModule", "HelloService") ;
    assertNotNull(helloService) ;
    assertEquals("NeverwinterDP", helloService.getServerGroup()) ;
    assertEquals("HelloService", helloService.getServiceRegistration().getServiceId()) ;
    assertEquals("hello property", helloService.getHelloProperty()) ;
    assertEquals("hello map property", helloService.getHelloProperties().get("hello")) ;
    assertNotNull(helloService.getComponentMonitor()) ;
    
    HelloService helloServiceInstance = 
      moduleContainer.getService(HelloModule.class.getSimpleName(), "HelloServiceInstance") ;
    assertEquals("HelloServiceInstance", helloServiceInstance.getServiceRegistration().getServiceId()) ;
    assertNotNull(helloServiceInstance.getComponentMonitor()) ;
    ServerRegistration sReg = server.getServerRegistration() ;
    assertTrue(sReg.getRoles().contains("master")) ;
    
    
    ClusterClient client = new HazelcastClusterClient() ;
    ClusterRegistration cReg = client.getClusterRegistration() ;
    assertEquals(1, cReg.findClusterMemberByRole("master").length) ;
  }
}
