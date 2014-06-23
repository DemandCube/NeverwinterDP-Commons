package com.neverwinterdp.hadoop.yarn.app;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neverwinterdp.hadoop.AbstractMiniClusterUnitTest;
import com.neverwinterdp.hadoop.yarn.app.AppClient;
import com.neverwinterdp.hadoop.yarn.app.AppClientMonitor;

public class AppClientUnitTest extends AbstractMiniClusterUnitTest {
  static {
    System.setProperty("java.net.preferIPv4Stack", "true") ;
  }
  
  MiniYARNCluster miniYarnCluster ;

  @Before
  public void setup() throws Exception {
    miniYarnCluster = createMiniYARNCluster(1);
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
      "--app-name", "Hello Yarn",
      "--container-manager", "com.neverwinterdp.hadoop.yarn.app.hello.HelloAppContainerManger",
      "--conf:yarn.resourcemanager.scheduler.address=0.0.0.0:8030"
    } ;
    AppClient appClient = new AppClient() ;
    AppClientMonitor reporter = 
        appClient.run(args, new YarnConfiguration(miniYarnCluster.getConfig()));
    reporter.monitor(); 
    reporter.report(System.out);
  }
}