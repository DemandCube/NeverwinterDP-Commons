package com.neverwinterdp.server.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.ServerRegistration;
import com.neverwinterdp.server.ServerState;
import com.neverwinterdp.server.cluster.hazelcast.HazelcastClusterClient;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.command.ServerCommands;
import com.neverwinterdp.server.command.ServiceCommand;
import com.neverwinterdp.server.command.ServiceCommandResult;
import com.neverwinterdp.server.command.ServiceCommands;
import com.neverwinterdp.server.service.ServiceRegistration;
import com.neverwinterdp.server.service.ServiceState;
import com.neverwinterdp.util.JSONSerializer;
import com.neverwinterdp.util.monitor.snapshot.ApplicationMonitorSnapshot;
import com.neverwinterdp.util.monitor.snapshot.ComponentMonitorSnapshot;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class ClusterServiceUnitTest {
  static {
    System.setProperty("app.dir", "build/cluster") ;
    System.setProperty("log4j.configuration", "file:src/test/resources/log4j.properties") ;
  }
  
  static protected Server[]      instance ;
  static protected ClusterClient client ;
  
  @BeforeClass
  static public void setup() throws Exception {
    String[] args = {
      "-Pserver.group=NeverwinterDP", "-Pserver.name=test", "-Pserver.roles=master"
    };
    instance = new Server[3] ;
    for(int i = 0; i < instance.length; i++) {
      instance[i] = Server.create(args) ;  
    }
    client = new HazelcastClusterClient() ;
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
      System.out.println(sel.getError());
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
      System.out.println(sel.getError());
    }
    assertEquals("Hello Tuan", sel.getResult()) ;

    ServiceCommand<ComponentMonitorSnapshot> monitorCall = new ServiceCommands.GetServiceMonitor() ;
    monitorCall.setTargetService(helloService);
    ServiceCommandResult<ComponentMonitorSnapshot> monitorCallResult = client.execute(monitorCall, member) ;
    assertFalse(monitorCallResult.hasError()) ;
    System.out.println("----Service Monitor Registry----");
    System.out.println(JSONSerializer.INSTANCE.toString(monitorCallResult.getResult()));
    System.out.println("--------------------------------");
    
    ClusterMember targetMember = instance[0].getClusterService().getMember() ;
    ServerCommandResult<ApplicationMonitorSnapshot> monitorResult = 
        client.execute(new ServerCommands.GetMonitorSnapshot(), targetMember) ;
    assertFalse(monitorResult.hasError()) ;
    System.out.println(JSONSerializer.INSTANCE.toString(monitorResult.getResult()));
  
    
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