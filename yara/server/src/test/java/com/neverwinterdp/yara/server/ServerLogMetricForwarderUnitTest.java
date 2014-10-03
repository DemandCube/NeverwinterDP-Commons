package com.neverwinterdp.yara.server;

import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.codahale.metrics.Clock;
import com.neverwinterdp.netty.rpc.client.RPCClient;
import com.neverwinterdp.netty.rpc.server.RPCServer;
import com.neverwinterdp.util.FileUtil;
import com.neverwinterdp.yara.MetricRegistry;
import com.neverwinterdp.yara.protocol.YaraService;
import com.neverwinterdp.yara.client.RPCLogRequestForwarder;
import com.neverwinterdp.yara.client.ServerLogMetricPlugin;
import com.neverwinterdp.yara.cluster.ClusterMetricPrinter;

public class ServerLogMetricForwarderUnitTest {
  static protected RPCServer server  ;
  static protected RPCClient client ;
  
  @BeforeClass
  static public void setup() throws Exception {
    server = new RPCServer() ;
    server.startAsDeamon(); 
    client = new RPCClient() ;
  }
  
  @AfterClass
  static public void teardown() {
    client.close();
    server.shutdown();
  }
  
  @Test
  public void testBasic() throws Exception {
    FileUtil.removeIfExist("build/ServerLogMetricPlugin", false);
    
    YaraServiceImpl yaraService = new YaraServiceImpl() ;
    server.getServiceRegistry().register(YaraService.newReflectiveBlockingService(yaraService));
    
    MetricRegistry server1 = createMetricRegistry("server1") ;
    MetricRegistry server2 = createMetricRegistry("server2") ;
    
    Random rand = new Random() ;
    for(int i = 0; i < 100000; i++) {
      long timestamp = Clock.defaultClock().getTick() ;
      
      server1.counter("counter").incr() ;
      server1.timer("timer").update(timestamp, rand.nextInt(1000)) ;
      
      server2.counter("counter").incr() ;
      server2.timer("timer").update(timestamp, rand.nextInt(1000)) ;
    }
    Thread.sleep(6000);
    new ClusterMetricPrinter().print(yaraService.getClusterMetricRegistry()); 
  }
  
  MetricRegistry createMetricRegistry(String server) throws Exception {
    RPCLogRequestForwarder forwarder = new RPCLogRequestForwarder(client) ;
    MetricRegistry registry = new MetricRegistry() ;
    ServerLogMetricPlugin plugin = new ServerLogMetricPlugin(server, "build/ServerLogMetricPlugin/" + server, forwarder) ;
    registry.getPluginManager().add(plugin);
    return registry ;
  }
}
