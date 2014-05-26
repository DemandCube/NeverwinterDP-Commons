package com.neverwinterdp.cluster;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.ServerRegistration;
import com.neverwinterdp.server.ServerState;
import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.cluster.ClusterEvent;
import com.neverwinterdp.server.cluster.ClusterListener;
import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.server.cluster.hazelcast.HazelcastClusterClient;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.command.ServerCommands;
import com.neverwinterdp.server.command.ServiceCommand;
import com.neverwinterdp.server.command.ServiceCommandResult;
import com.neverwinterdp.server.command.ServiceCommands;
import com.neverwinterdp.server.service.HelloServiceContainerModule;
import com.neverwinterdp.server.service.ServiceRegistration;
import com.neverwinterdp.server.service.ServiceState;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class ClusterServiceUnitTest {
  static {
    System.setProperty("app.dir", "build/cluster") ;
    System.setProperty("log4j.configuration", "file:src/main/resources/log4j.properties") ;
  }
  
  static protected Server[]      instance ;
  static protected ClusterClient client ;
  
  @BeforeClass
  static public void setup() throws Exception {
    Properties properties = new Properties() ;
    properties.put("server.group", "NeverwinterDP") ;
    properties.put("server.cluster-framework", "hazelcast") ;
    properties.put("server.roles", "master") ;
    properties.put("server.service-container-module", HelloServiceContainerModule.class.getName()) ;
    
    instance = new Server[3] ;
    for(int i = 0; i < instance.length; i++) {
      instance[i] = Server.create(properties) ;  
    }
    ClusterMember member = instance[1].getClusterService().getMember() ;
    String connectUrl = member.getIpAddress() + ":" + member.getPort() ;
    client = new HazelcastClusterClient(connectUrl) ;
  }

  @AfterClass
  static public void teardown() throws Exception {
    client.shutdown(); 
    for(int i = 0; i < instance.length; i++) {
      instance[i].exit(0) ;
    }
    Thread.sleep(1000);
  }
  
  @Test
  public void testPingService() {
    Util.assertServerState(client, ServerState.RUNNING) ;
    ClusterMember member = instance[0].getClusterService().getMember() ;
    ServerRegistration serverRegistration = client.getServerRegistration(member);
    ServiceRegistration helloService = serverRegistration.getServices().get(0) ; 
    System.out.println("Service ID = " + helloService.getServiceId());
    
    ServiceCommand<ServiceState> ping = new ServiceCommands.Ping().setLogEnable(true) ;
    ping.setTargetService(helloService);
    ServiceCommandResult<ServiceState> sel = client.execute(ping, member) ;
    if(sel.hasError()) {
      sel.getError().printStackTrace(); 
      fail() ;
    }
    assertEquals(ServiceState.START, sel.getResult()) ;
  }
  
  @Test
  public void testStopStartService() throws Exception {
    ServiceClusterListener<ClusterClient> listener = new ServiceClusterListener<ClusterClient>() ;
    client.addListener(listener);
    
    ClusterMember member = instance[0].getClusterService().getMember() ;
    ServerRegistration serverRegistration = 
      client.getClusterRegistration().getServerRegistration(member);
    ServiceRegistration helloService = serverRegistration.getServices().get(0) ; 
    
    ServiceCommand<ServiceRegistration> stop = new ServiceCommands.Stop().setLogEnable(true) ;
    stop.setTargetService(helloService);
    ServiceCommandResult<ServiceRegistration> stopResult = client.execute(stop, member) ;
    assertEquals(ServiceState.STOP, stopResult.getResult().getState()) ;
    //wait to make sure the client get a cluster service stop event notification
    Thread.sleep(100l);
    assertEquals(1, listener.events.size()) ;
    
    ServiceCommand<ServiceRegistration> start = new ServiceCommands.Start().setLogEnable(true) ;
    start.setTargetService(helloService);
    ServiceCommandResult<ServiceRegistration> startResult = client.execute(start, member) ;
    assertEquals(ServiceState.START, startResult.getResult().getState()) ;
    //wait to make sure the client get a cluster service stop event notification
    Thread.sleep(100l);
    assertEquals(2, listener.events.size()) ;
    
    client.removeListener(listener);
  }
  
  @Test
  public void testMethodCall() {
    Util.assertServerState(client, ServerState.RUNNING) ;
    ClusterMember member = instance[0].getClusterService().getMember() ;
    ServerRegistration serverRegistration = client.getServerRegistration(member);
    ServiceRegistration helloService = serverRegistration.getServices().get(0) ; 
    
    ServiceCommand<String> helloCall = new ServiceCommands.MethodCall<String>("hello", "Tuan") ;
    helloCall.setTargetService(helloService);
    ServiceCommandResult<String> sel = client.execute(helloCall, member) ;
    if(sel.hasError()) {
      sel.getError().printStackTrace();
    }
    assertEquals("Hello Tuan", sel.getResult()) ;
    
    ClusterMember targetMember = instance[0].getClusterService().getMember() ;
    ServerCommandResult<String> monitorResult = 
        client.execute(new ServerCommands.GetMonitorRegistry(), targetMember) ;
    assertFalse(monitorResult.hasError()) ;
    System.out.println(monitorResult.getResult());
  }
  
  static public class ServiceClusterListener<T> implements ClusterListener<T> {
    List<ClusterEvent> events = new ArrayList<ClusterEvent>() ;
    
    public void onEvent(T listener, ClusterEvent event) { 
      if(event.getType().equals(ClusterEvent.ServiceStateChange)) {
        events.add(event) ;
      }
    }
  }
}