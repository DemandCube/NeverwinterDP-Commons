package com.neverwinterdp.hadoop;

import java.io.File;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.apache.hadoop.service.Service.STATE;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.junit.Assert;

public class AbstractMiniClusterUnitTest {
  
  static protected MiniYARNCluster createMiniYARNCluster(int numOfNodeManagers) throws Exception {
    YarnConfiguration conf = new YarnConfiguration() ;
    MiniYARNCluster cluster = createMiniYARNCluster(conf, numOfNodeManagers) ;
    long stopTime = System.currentTimeMillis() + 30000;
    while(stopTime < System.currentTimeMillis() && !cluster.isInState(STATE.STARTED)) {
      Thread.sleep(100);
    }
    if(!cluster.isInState(STATE.STARTED)) {
      throw new Exception("Cannot start the mini cluster after 25s") ;
    }
    return cluster ;
  }
  
  static protected MiniYARNCluster createMiniYARNCluster(Configuration yarnConf, int numOfNodeManagers) throws Exception {
    yarnConf.setInt(YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_MB, 64);
    yarnConf.setClass(YarnConfiguration.RM_SCHEDULER, FifoScheduler.class, ResourceScheduler.class);
    MiniYARNCluster miniYarnCluster = new MiniYARNCluster("yarn", numOfNodeManagers, 1, 1);
    miniYarnCluster.init(yarnConf);
    yarnConf.set("yarn.resourcemanager.scheduler.address", "0.0.0.0:8030") ;
    miniYarnCluster.start();
    return miniYarnCluster ;
  }
  
  static protected MiniDFSCluster createMiniDFSCluster(String dir, int numDataNodes) throws Exception {
    return createMiniDFSCluster(new Configuration(), dir, numDataNodes) ;
  }
  
  static protected MiniDFSCluster createMiniDFSCluster(Configuration conf, String dir, int numDataNodes) throws Exception {
    File baseDir = new File(dir).getAbsoluteFile();
    FileUtil.fullyDelete(baseDir);
    conf.set(MiniDFSCluster.HDFS_MINIDFS_BASEDIR, baseDir.getAbsolutePath());
    MiniDFSCluster hdfsCluster =
        new MiniDFSCluster.Builder(conf).
        nnTopology(MiniDFSNNTopology.simpleSingleNN(8020, 50070)).
        numDataNodes(numDataNodes).
        build();
    hdfsCluster.waitClusterUp();
    String hdfsURI = "hdfs://localhost:" + hdfsCluster.getNameNodePort() + "/";
    System.out.println("hdfs uri: " + hdfsURI) ;
    FileSystem fs = hdfsCluster.getFileSystem();
    Assert.assertTrue("Not a HDFS: "+ fs.getUri(), fs instanceof DistributedFileSystem);
    //final DistributedFileSystem dfs = (DistributedFileSystem)fs;
    //dfs.copyFromLocalFile(false, false, new Path("target/hadoop-samples-1.0.jar"), new Path("/tmp/hadoop-samples-1.0.jar"));
    return hdfsCluster ;
  }
  
}
