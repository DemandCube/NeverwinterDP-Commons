package com.neverwinterdp.cluster.hazelcast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.ServerState;
import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.server.cluster.hazelcast.HazelcastClusterClient;
import com.neverwinterdp.server.command.ServerCommand;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.command.ServerCommands;
import com.neverwinterdp.server.service.HelloServiceModule;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class HazelcastClusterUnitTest {
  static Server[] instance ;
  static ClusterClient client ;
  
  @BeforeClass
  static public void setup() {
    Properties properties = new Properties() ;
    properties.put("server.group", "NeverwinterDP") ;
    properties.put("server.cluster-framework", "hazelcast") ;
    properties.put("server.roles", "master") ;
    properties.put("server.service-module", HelloServiceModule.class.getName()) ;
    
    instance = new Server[3] ;
    for(int i = 0; i < instance.length; i++) {
      instance[i] = Server.create(properties);  
    }
    ClusterMember member = instance[1].getClusterService().getMember() ;
    String connectUrl = member.getIpAddress() + ":" + member.getPort() ;
    client = new HazelcastClusterClient(connectUrl) ;
  }
  
  @AfterClass
  static public void teardown() {
    client.shutdown();
    for(int i = 0; i < instance.length; i++) {
      instance[i].onDestroy();;
    }
    client.shutdown(); 
  }
  
  @Test
  public void testPing() throws Exception {
    ServerCommand<ServerState> ping = new ServerCommands.Ping() ;
    ping.setTimeout(10000l);
    ClusterMember targetMember = instance[1].getClusterService().getMember() ;
    ServerCommandResult<ServerState> result = instance[0].getClusterService().execute(ping, targetMember) ;
    if(result.hasError()) {
      result.getError().printStackTrace() ;
    }
    assertFalse(result.hasError()) ;
    assertEquals(ServerState.RUNNING, result.getResult()) ;
    
    ClusterMember[] allMember = new ClusterMember[instance.length] ;
    for(int i = 0 ; i < allMember.length; i++) {
      allMember[i] = instance[i].getClusterService().getMember() ;
    }
    ServerCommandResult<ServerState>[] results = client.execute(ping, allMember) ;
    for(ServerCommandResult<ServerState> sel : results) {
      assertFalse(sel.hasError()) ;
      assertEquals(ServerState.RUNNING, sel.getResult()) ;
    }
    
  }
}
