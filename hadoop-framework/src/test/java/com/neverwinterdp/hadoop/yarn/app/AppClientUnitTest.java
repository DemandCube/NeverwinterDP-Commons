package com.neverwinterdp.hadoop.yarn.app;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neverwinterdp.hadoop.MiniClusterUtil;
import com.neverwinterdp.hadoop.yarn.app.protocol.IPCService;
import com.neverwinterdp.hadoop.yarn.app.protocol.Void;
import com.neverwinterdp.netty.rpc.client.DefaultClientRPCController;

public class AppClientUnitTest extends MiniClusterUtil {
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
      "--app-container-manager", "com.neverwinterdp.hadoop.yarn.sample.HelloAppContainerManger",
      "--app-rpc-port", "63200" ,
      "--app-history-server-address", "http://127.0.0.1:9090/yarn-app/history",
      "--conf:yarn.resourcemanager.scheduler.address=0.0.0.0:8030"
    } ;
    AppClient appClient = new AppClient() ;
    AppClientMonitor reporter = 
        appClient.run(args, new YarnConfiguration(miniYarnCluster.getConfig()));
    
    IPCService.BlockingInterface ipcService = reporter.getIPCService() ;
    System.out.println("Status: " + ipcService.getAppMasterStatus(new DefaultClientRPCController(), Void.getDefaultInstance())) ;
    reporter.monitor(); 
    reporter.report(System.out);
  }
}