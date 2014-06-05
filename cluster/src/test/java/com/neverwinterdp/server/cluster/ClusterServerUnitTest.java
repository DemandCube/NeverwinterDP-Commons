package com.neverwinterdp.server.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.server.ActivityLog;
import com.neverwinterdp.server.ActivityLogs;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.ServerState;
import com.neverwinterdp.server.cluster.hazelcast.HazelcastClusterClient;
import com.neverwinterdp.server.command.ActivityLogsCommand;
import com.neverwinterdp.server.command.ServerCommand;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.command.ServerCommands;
import com.neverwinterdp.server.service.ServiceRegistration;
import com.neverwinterdp.util.JSONSerializer;
import com.neverwinterdp.util.monitor.snapshot.ApplicationMonitorSnapshot;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class ClusterServerUnitTest {
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
  }
  
  @Test
  public void assertServerEnvironment() throws Exception {
    assertEquals(instance.length, client.getClusterRegistration().getNumberOfServers()) ;
    Map<ClusterMember, ServiceRegistration> helloServiceMap = 
      client.getClusterRegistration().findByServiceId("HelloModule", "HelloService") ;
    assertEquals(instance.length, helloServiceMap.size()) ;
  }
  
  @Test
  public void testPing() throws Exception {
    assertEquals(instance.length, client.getClusterRegistration().getNumberOfServers()) ;
    Util.assertServerState(client, ServerState.RUNNING) ;
    ServerCommand<ServerState> ping = new ServerCommands.Ping() ;
    ClusterMember member = instance[0].getClusterService().getMember() ;
    Thread.sleep(1000);
    System.out.println("----------------------------------------------") ;
    ServerCommandResult<ServerState> singleMemberPing = client.execute(ping, member) ;
    assertFalse(singleMemberPing.hasError()) ;
    assertEquals(ServerState.RUNNING, singleMemberPing.getResult()) ;
    
    Thread.sleep(1000);
    System.out.println("----------------------------------------------") ;
    client.execute(ping, new ClusterMember[] { member }) ;
  }
  
  @Test
  public void testGetActivityLogs() throws Exception {
    Util.assertServerState(client, ServerState.RUNNING) ;
    
    ServerCommandResult<ActivityLogs>[] results = 
      client.execute(new ActivityLogsCommand.Get().setLogEnable(true)) ;
    for(ServerCommandResult<ActivityLogs> sel : results) {
      assertFalse(sel.hasError()) ;
      ActivityLogs activityLogs = sel.getResult() ;
      assertEquals(1, activityLogs.find(ActivityLog.Command, "Ping").size()) ;
    }
  }
  
  @Test
  public void testClearActivityLogs() throws Exception {
    Util.assertServerState(client, ServerState.RUNNING) ;
    client.execute(new ActivityLogsCommand.Clear()) ;
    
    ServerCommandResult<ActivityLogs>[] results = client.execute(new ActivityLogsCommand.Get()) ;
    for(ServerCommandResult<ActivityLogs> sel : results) {
      assertFalse(sel.hasError()) ;
      ActivityLogs activityLogs = sel.getResult() ;
      assertEquals(0, activityLogs.size()) ;
    }
  }
  
  @Test
  public void testStartShutDown() throws Exception {
    Util.assertServerState(client, ServerState.RUNNING) ;
    client.execute(new ActivityLogsCommand.Clear()) ;
    
    ServerCommandResult<ServerState>[] shutdownResults = 
      client.execute(new ServerCommands.Shutdown().setLogEnable(true)) ;
    for(ServerCommandResult<ServerState> sel : shutdownResults) {
      assertFalse(sel.hasError()) ;
      ServerState serverState = sel.getResult() ;
      assertEquals(ServerState.SHUTDOWN, serverState) ;
    }
    Util.assertServerState(client, ServerState.SHUTDOWN) ;
    
    ServerCommandResult<ServerState>[] startResults = 
      client.execute(new ServerCommands.Start().setLogEnable(true)) ;
    for(ServerCommandResult<ServerState> sel : startResults) {
      assertFalse(sel.hasError()) ;
      ServerState serverState = sel.getResult() ;
      assertEquals(ServerState.RUNNING, serverState) ;
    }
    Util.assertServerState(client, ServerState.RUNNING) ;
  }
  
  @Test
  public void testShutdownRunningEvent() throws Exception {
    Util.assertServerState(client, ServerState.RUNNING) ;
    client.execute(new ActivityLogsCommand.Clear()) ;

    ClusterMember targetMember = instance[0].getClusterService().getMember() ;
    ServerCommandResult<ServerState> shutdownResult = 
        client.execute(new ServerCommands.Shutdown().setLogEnable(true), targetMember) ;
    assertFalse(shutdownResult.hasError()) ;
    assertEquals(ServerState.SHUTDOWN, shutdownResult.getResult()) ;
    //wait to make sure the event are broadcasted to the other nodes
    Thread.sleep(1000); 
    Util.assertActivityLogs(client, "Server Shutdown Event", ActivityLog.ClusterEvent, "ServerStateChange", "SHUTDOWN", 1) ;

    client.execute(new ActivityLogsCommand.Clear()) ;
    ServerCommandResult<ServerState> startResult = 
        client.execute(new ServerCommands.Start().setLogEnable(true), targetMember) ;
    assertFalse(startResult.hasError()) ;
    assertEquals(ServerState.RUNNING, startResult.getResult()) ;
    //wait to make sure the event are broadcasted to the other nodes
    Thread.sleep(1000); 
    Util.assertActivityLogs(client, "Server Start Event", ActivityLog.ClusterEvent, "ServerStateChange", "RUNNING", 1) ;
  }
  
  @Test
  public void testMonitorRegistry() throws Exception {
    Util.assertServerState(client, ServerState.RUNNING) ;
    ClusterMember targetMember = instance[0].getClusterService().getMember() ;
    ServerCommandResult<ApplicationMonitorSnapshot> monitorResult = 
        client.execute(new ServerCommands.GetMonitorSnapshot(), targetMember) ;
    assertFalse(monitorResult.hasError()) ;
    System.out.println(JSONSerializer.INSTANCE.toString(monitorResult.getResult()));
  }
}