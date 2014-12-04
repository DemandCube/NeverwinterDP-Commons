package com.neverwinterdp.hadoop;

import static org.junit.Assert.assertNotNull;

import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.junit.Test;

public class MiniClusterUnitTest extends MiniClusterUtil {
  @Test
  public void testMiniHDFSCluster() throws Exception {
    MiniDFSCluster cluster = createMiniDFSCluster("build/hadoop", 2) ;
    assertNotNull(cluster) ;
    cluster.shutdown(true);
  }

  @Test
  public void testMiniYarnCluster() throws Exception {
    MiniYARNCluster miniYarnCluster = createMiniYARNCluster(2) ;
    assertNotNull(miniYarnCluster) ;
    miniYarnCluster.stop();
    miniYarnCluster.close();
  }
}
