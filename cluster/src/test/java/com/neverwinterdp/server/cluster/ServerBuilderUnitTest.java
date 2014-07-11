package com.neverwinterdp.server.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.neverwinterdp.server.MultiServer;
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
      "-Pserver.group=NeverwinterDP", "-Pserver.name=test", "-Pserver.roles=test"
    };
    Server server = Server.create(args);
    Thread.sleep(1000);
    ClusterClient client = new HazelcastClusterClient() ;
    assertServer(server, client, "test") ;
  }
  
  @Test
  public void testMultiBuilder() throws Exception {
    String[] args = {
      "-Ptest1:server.group=NeverwinterDP", "-Ptest1:server.name=test1", "-Ptest1:server.roles=test1",
      "-Ptest2:server.group=NeverwinterDP", "-Ptest2:server.name=test2", "-Ptest2:server.roles=test2"
    };
    
    Server[] servers = MultiServer.create(args);
    Thread.sleep(3000);
    ClusterClient client = new HazelcastClusterClient() ;
    
    assertEquals(2, servers.length) ;
    for(int i = 0; i < servers.length; i++) {
      String name = "test" + (i + 1) ;
      assertServer(servers[i], client, name) ;
    }
    client.shutdown(); 
    for(Server server : servers) server.destroy();
  }
  
  void assertServer(Server server, ClusterClient client, String name) throws Exception {
    System.out.println("----------------------------------------");
    ServerConfig config = server.getConfig() ;
    assertNotNull(config) ;
    assertEquals(name, config.getServerName()) ;
    
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
    assertTrue(sReg.getRoles().contains(name)) ;
    
    ClusterRegistration cReg = client.getClusterRegistration() ;
    assertEquals(1, cReg.findClusterMemberByRole(name).length) ;
  }
}
