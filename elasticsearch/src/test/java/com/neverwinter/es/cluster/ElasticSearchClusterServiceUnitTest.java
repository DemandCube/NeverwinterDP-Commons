package com.neverwinter.es.cluster;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ElasticSearchClusterServiceUnitTest {
  static ElasticSearchClusterBuilder clusterBuilder ;
  
  @BeforeClass
  static public void setup() throws Exception {
    clusterBuilder = new ElasticSearchClusterBuilder() ;
  }

  @AfterClass
  static public void teardown() throws Exception {
    clusterBuilder.destroy();
  }
  
  @Test
  public void test() throws Exception {
    clusterBuilder.install();
    Thread.sleep(10000);
    clusterBuilder.uninstall();
  }
}
