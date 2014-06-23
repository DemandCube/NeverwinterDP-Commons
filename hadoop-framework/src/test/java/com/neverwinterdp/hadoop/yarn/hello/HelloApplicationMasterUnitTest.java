package com.neverwinterdp.hadoop.yarn.hello;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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
import org.apache.hadoop.yarn.util.Apps;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neverwinterdp.hadoop.AbstractMiniClusterUnitTest;

public class HelloApplicationMasterUnitTest extends AbstractMiniClusterUnitTest {

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
  public void testMiniYarnCluster() throws Exception {
    String[] args = { "/bin/date", "2" } ;
    run(args);
  }

  public void run(String[] args) throws Exception {
    final String command = args[0];
    final int n = Integer.valueOf(args[1]);
    
    //No need to upload jar since the class is already include in the classpath
    //final Path jarPath = new Path(args[2]);

    // Create yarnClient
    Configuration conf = new YarnConfiguration(miniYarnCluster.getConfig());
    YarnClient yarnClient = YarnClient.createYarnClient();
    yarnClient.init(conf);
    yarnClient.start();

    // Create application via yarnClient
    YarnClientApplication app = yarnClient.createApplication();

    // Set up the container launch context for the application master
    ContainerLaunchContext amContainer = Records.newRecord(ContainerLaunchContext.class);
//    amContainer.setCommands(
//        Collections.singletonList(
//            "java -Xmx128M " +
//                HelloApplicationMaster.class.getName() +
//                " " + command +
//                " " + String.valueOf(n) + 
//                " 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout" + 
//                " 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr" 
//            )
//        );
    
    amContainer.setCommands(
      Collections.singletonList(
        command +
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
    Resource resource = Records.newRecord(Resource.class);
    resource.setMemory(256);
    resource.setVirtualCores(1);

    System.out.println("Finally, set-up ApplicationSubmissionContext for the application");
    ApplicationSubmissionContext appContext = app.getApplicationSubmissionContext();
    appContext.setApplicationName("simple-yarn-app"); // application name
    appContext.setAMContainerSpec(amContainer);
    appContext.setResource(resource);
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
    assertEquals(YarnApplicationState.FINISHED, appState) ;
    System.out.println(
      "Application " + appId + " finished with state " + appState + 
      " at " + appReport.getFinishTime()
    );
  }
  
  private void assertEquals(YarnApplicationState finished, YarnApplicationState appState) {
    // TODO Auto-generated method stub
    
  }

  //TODO: need to setup the upload jar in the real cluster 
  void setupAppMasterJar(Configuration conf, Path jarPath, LocalResource appMasterJar) throws IOException {
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
