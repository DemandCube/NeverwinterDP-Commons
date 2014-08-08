package com.neverwinterdp.zookeeper.cluster;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class ZookeeperUnitTest {
  static ZookeeperClusterBuilder clusterBuilder ;

  @BeforeClass
  static public void setup() throws Exception {
    clusterBuilder = new ZookeeperClusterBuilder() ;
  }

  @AfterClass
  static public void teardown() throws Exception {
    clusterBuilder.destroy();
  }
  
  @Test
  public void testZookeeper() throws Exception {
    //TODO: write some test such create zk node here
  }
}
