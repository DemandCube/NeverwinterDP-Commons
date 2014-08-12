package com.neverwinterdp.hadoop.yarn.app;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neverwinterdp.hadoop.AbstractMiniClusterUnitTest;
import com.neverwinterdp.hadoop.yarn.app.AppClient;
import com.neverwinterdp.hadoop.yarn.app.AppClientMonitor;
import com.neverwinterdp.hadoop.yarn.app.master.ipc.AppMasterRPC;
import com.neverwinterdp.util.JSONSerializer;

public class AppClientUnitTest extends AbstractMiniClusterUnitTest {
  static {
    System.setProperty("java.net.preferIPv4Stack", "true") ;
  }
  
  MiniYARNCluster miniYarnCluster ;

  @Before
  public void setup() throws Exception {
    YarnConfiguration yarnConf = new YarnConfiguration() ;
    yarnConf.set("io.serializations", "org.apache.hadoop.io.serializer.JavaSerialization");
    miniYarnCluster = createMiniYARNCluster(yarnConf, 1);
    Configuration conf = miniYarnCluster.getConfig() ;
  }

  @After
  public void teardown() throws Exception {
    miniYarnCluster.stop();
    miniYarnCluster.close();
  }

  @Test
  public void testAppClient() throws Exception {
    String[] args = { 
      "--mini-cluster-env",
      "--app-name", "HelloYarn",
      "--app-container-manager", "com.neverwinterdp.hadoop.yarn.app.hello.HelloAppContainerManger",
      "--app-rpc-port", "63200" ,
      "--conf:yarn.resourcemanager.scheduler.address=0.0.0.0:8030"
    } ;
    AppClient appClient = new AppClient() ;
    AppClientMonitor reporter = 
        appClient.run(args, new YarnConfiguration(miniYarnCluster.getConfig()));
    
    AppMasterRPC appMasterRPC = reporter.getAppMasterRPC() ;
    System.out.println("PING: " + appMasterRPC.ping("hello")) ;
    System.out.println(JSONSerializer.INSTANCE.toString(appMasterRPC.getAppMasterMonitorAsJSON())) ;
    reporter.monitor(); 
    reporter.report(System.out);
  }
}