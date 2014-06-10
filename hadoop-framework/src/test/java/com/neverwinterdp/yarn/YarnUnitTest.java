package com.neverwinterdp.yarn;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.junit.Test;

public class YarnUnitTest {
  @Test
  public void testMiniHDFSCluster() throws Exception {
    FileUtils.deleteDirectory(new File("target/hadoop"));
    System.setProperty("java.io.tmpdir", "target/tmp");
    Configuration conf = new Configuration();
    conf.set("hadoop.tmp.dir", "target/hadoop");
    conf.setBoolean("dfs.permissions", false);
    conf.set("dfs.namenode.name.dir", "file:build/hadoop/name");
    conf.set("dfs.datanode.data.dir", "file:build/hadoop/dfs");
    File baseDir = new File("./build/hadoop").getAbsoluteFile();
    FileUtil.fullyDelete(baseDir);
    conf.set(MiniDFSCluster.HDFS_MINIDFS_BASEDIR, baseDir.getAbsolutePath());
    MiniDFSCluster hdfsCluster =
        new MiniDFSCluster.Builder(conf).
            nnTopology(MiniDFSNNTopology.simpleSingleNN(8020, 50070)).
            numDataNodes(1).
            build();

    String hdfsURI = "hdfs://localhost:" + hdfsCluster.getNameNodePort() + "/";
  }

  @Test
  public void testMiniYarnCluster() throws Exception {
    System.setProperty("hadoop.home.dir", "d:/java/hadoop-2.4.0");
    System.setProperty("java.io.tmpdir", "build/tmp");
    YarnConfiguration clusterConf = new YarnConfiguration();
    clusterConf.setInt(YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_MB, 64);
    clusterConf.setClass(YarnConfiguration.RM_SCHEDULER, FifoScheduler.class, ResourceScheduler.class);
    MiniYARNCluster miniYarnCluster = new MiniYARNCluster("yarn", 1, 1, 1);
    miniYarnCluster.init(clusterConf);
    miniYarnCluster.start();
    miniYarnCluster.stop();
    miniYarnCluster.close();
    // once the cluster is created, you can get its configuration
    // with the binding details to the cluster added from the minicluster
    YarnConfiguration appConf = new YarnConfiguration(miniYarnCluster.getConfig());
  }
}
