package com.neverwinterdp.hadoop.yarn.lih;

import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neverwinterdp.hadoop.AbstractMiniClusterUnitTest;
import com.neverwinterdp.hadoop.yarn.AppClient;
import com.neverwinterdp.hadoop.yarn.AppClientReporter;

public class AppClientUnitTest extends AbstractMiniClusterUnitTest {
  MiniYARNCluster miniYarnCluster ;

  @Before
  public void setup() throws Exception {
    miniYarnCluster = createMiniYARNCluster(1);
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
      "--command", "java com.neverwinterdp.hadoop.yarn.lih.SampleAM -container_mem 300 --container_cnt 1 --command date" 
    } ;
    AppClient appClient = new AppClient() ;
    AppClientReporter reporter = 
        appClient.run(args, new YarnConfiguration(miniYarnCluster.getConfig()));
    reporter.monitor(); 
  }
}
