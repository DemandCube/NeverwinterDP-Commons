package com.neverwinterdp.yarn;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.apache.hadoop.yarn.util.Apps;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HelloApplicationMasterUnitTest {

  MiniDFSCluster hdfsCluster ;
  String hdfsURI ;
  MiniYARNCluster miniYarnCluster ;

  @Before
  public void setup() throws Exception {
    FileUtils.deleteDirectory(new File("target/hadoop"));
    System.setProperty("java.io.tmpdir", "target/tmp");
    Configuration conf = new Configuration();
    conf.set("hadoop.tmp.dir", "target/hadoop");
    conf.setBoolean("dfs.permissions", false);
    conf.set("dfs.namenode.name.dir", "file:target/hadoop/name");
    conf.set("dfs.datanode.data.dir", "file:target/hadoop/dfs");
    File baseDir = new File("./target/hadoop").getAbsoluteFile();
    FileUtil.fullyDelete(baseDir);
    conf.set(MiniDFSCluster.HDFS_MINIDFS_BASEDIR, baseDir.getAbsolutePath());
    hdfsCluster =
        new MiniDFSCluster.Builder(conf).
        nnTopology(MiniDFSNNTopology.simpleSingleNN(8020, 50070)).
        numDataNodes(1).
        build();
    hdfsURI = "hdfs://localhost:" + hdfsCluster.getNameNodePort() + "/";
    FileSystem fs = hdfsCluster.getFileSystem();
    Assert.assertTrue("Not a HDFS: "+fs.getUri(),fs instanceof DistributedFileSystem);
    //final DistributedFileSystem dfs = (DistributedFileSystem)fs;
    //dfs.copyFromLocalFile(false, false, new Path("target/hadoop-samples-1.0.jar"), new Path("/tmp/hadoop-samples-1.0.jar"));
    
    YarnConfiguration yarnConf = new YarnConfiguration();
    yarnConf.setInt(YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_MB, 64);
    yarnConf.setClass(YarnConfiguration.RM_SCHEDULER, FifoScheduler.class, ResourceScheduler.class);
    miniYarnCluster = new MiniYARNCluster("yarn", 1, 1, 1);
    miniYarnCluster.init(yarnConf);
    yarnConf.set("yarn.resourcemanager.scheduler.address", "0.0.0.0:8030") ;
    miniYarnCluster.start();
  }

  @After
  public void teardown() throws Exception {
    hdfsCluster.shutdown() ;
    miniYarnCluster.stop();
    miniYarnCluster.close();
  }

  @Test
  public void testMiniYarnCluster() throws Exception {
    String[] args = { "/bin/date", "2","file:///Users/Tuan/Projects/DemandCube/NeverwinterDP/hadoop-samples/target/hadoop-samples-1.0.jar" } ;
    run(args);
  }

  Configuration conf ;

  public void run(String[] args) throws Exception {
    final String command = args[0];
    final int n = Integer.valueOf(args[1]);
    //final Path jarPath = new Path(args[2]);

    // Create yarnClient
    conf = new YarnConfiguration(miniYarnCluster.getConfig());
    YarnClient yarnClient = YarnClient.createYarnClient();
    yarnClient.init(conf);
    yarnClient.start();

    // Create application via yarnClient
    YarnClientApplication app = yarnClient.createApplication();

    // Set up the container launch context for the application master
    ContainerLaunchContext amContainer = 
        Records.newRecord(ContainerLaunchContext.class);
    amContainer.setCommands(
        Collections.singletonList(
            "java -Xmx128M" +
                " com.neverwinterdp.yarn.HelloApplicationMaster" +
                " " + command +
                " " + String.valueOf(n) + 
                " 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout" + 
                " 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr" 
            )
        );

    // Setup jar for ApplicationMaster
    LocalResource appMasterJar = Records.newRecord(LocalResource.class);
    //setupAppMasterJar(jarPath, appMasterJar);
    //amContainer.setLocalResources(Collections.singletonMap("simpleapp.jar", appMasterJar));

    System.out.println("Setup CLASSPATH for ApplicationMaster") ;
    Map<String, String> appMasterEnv = new HashMap<String, String>();
    setupAppMasterEnv(appMasterEnv);
    amContainer.setEnvironment(appMasterEnv);

    System.out.println("Set up resource type requirements for ApplicationMaster") ;
    Resource capability = Records.newRecord(Resource.class);
    capability.setMemory(256);
    capability.setVirtualCores(1);

    System.out.println("Finally, set-up ApplicationSubmissionContext for the application");
    ApplicationSubmissionContext appContext = app.getApplicationSubmissionContext();
    appContext.setApplicationName("simple-yarn-app"); // application name
    appContext.setAMContainerSpec(amContainer);
    appContext.setResource(capability);
    appContext.setQueue("default"); // queue 

    // Submit application
    ApplicationId appId = appContext.getApplicationId();
    System.out.println("Submitting application " + appId);
    yarnClient.submitApplication(appContext);

    ApplicationReport appReport = yarnClient.getApplicationReport(appId);
    YarnApplicationState appState = appReport.getYarnApplicationState();
    while (appState != YarnApplicationState.FINISHED && 
           appState != YarnApplicationState.KILLED && 
           appState != YarnApplicationState.FAILED) {
      Thread.sleep(100);
      appReport = yarnClient.getApplicationReport(appId);
      appState = appReport.getYarnApplicationState();
    }

    System.out.println(
        "Application " + appId + " finished with" +
        " state " + appState + 
        " at " + appReport.getFinishTime()
    );
  }

  private void setupAppMasterJar(Path jarPath, LocalResource appMasterJar) throws IOException {
    FileStatus jarStat = FileSystem.get(conf).getFileStatus(jarPath);
    appMasterJar.setResource(ConverterUtils.getYarnUrlFromPath(jarPath));
    appMasterJar.setSize(jarStat.getLen());
    appMasterJar.setTimestamp(jarStat.getModificationTime());
    appMasterJar.setType(LocalResourceType.FILE);
    appMasterJar.setVisibility(LocalResourceVisibility.PUBLIC);
  }

  private void setupAppMasterEnv(Map<String, String> appMasterEnv) {
    String cps = System.getProperty("java.class.path") ;
    String[] cp = cps.split(":") ;
    for(String selCp : cp) {
      System.out.println("Add classpath: " + selCp);
      Apps.addToEnvironment(appMasterEnv, Environment.CLASSPATH.name(), selCp, ":");
    }
    Apps.addToEnvironment(appMasterEnv, Environment.CLASSPATH.name(), Environment.PWD.$() + File.separator + "*", ":");
  }
}
