package com.neverwinterdp.hadoop.yarn;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.Apps;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;

import com.beust.jcommander.JCommander;

public class AppClient  {
  
  //TODO: need to setup the upload jar in the real cluster 
  void setupAppMasterJar(Configuration conf, String jarFile, LocalResource appMasterJar) throws IOException {
    Path jarPath = new Path(jarFile) ;
    FileStatus jarStat = FileSystem.get(conf).getFileStatus(jarPath);
    appMasterJar.setResource(ConverterUtils.getYarnUrlFromPath(jarPath));
    appMasterJar.setSize(jarStat.getLen());
    appMasterJar.setTimestamp(jarStat.getModificationTime());
    appMasterJar.setType(LocalResourceType.FILE);
    appMasterJar.setVisibility(LocalResourceVisibility.PUBLIC);
  }

  void setupAppMasterEnv(AppOptions appOpts, Configuration conf, Map<String, String> appMasterEnv) {
    if(appOpts.miniClusterEnv) {
      String cps = System.getProperty("java.class.path") ;
      String[] cp = cps.split(":") ;
      for(String selCp : cp) {
        System.out.println("Add classpath: " + selCp);
        Apps.addToEnvironment(appMasterEnv, Environment.CLASSPATH.name(), selCp, ":");
      }
    } else {
      StringBuilder classPathEnv = new StringBuilder();
      classPathEnv.append(Environment.CLASSPATH.$()).append(File.pathSeparatorChar);
      classPathEnv.append("./*");

      String[] classpath = conf.getStrings(
          YarnConfiguration.YARN_APPLICATION_CLASSPATH,
          YarnConfiguration.DEFAULT_YARN_APPLICATION_CLASSPATH
      ) ;
      for (String selClasspath : classpath) {
        classPathEnv.append(File.pathSeparatorChar);
        classPathEnv.append(selClasspath.trim());
      }

      String envStr = classPathEnv.toString();
      appMasterEnv.put(Environment.CLASSPATH.name(), envStr);
    }
    Apps.addToEnvironment(appMasterEnv, Environment.CLASSPATH.name(), Environment.PWD.$() + File.separator + "*", ":");
  }
  
  public AppClientReporter run(String[] args) throws Exception {
    return run(args, new YarnConfiguration()) ;
  }
  
  public AppClientReporter run(String[] args, Configuration conf) throws Exception {
    AppOptions appOpts = new AppOptions() ;
    new JCommander(appOpts, args) ;
    
    System.out.println("Create YarnClient") ;
    YarnClient yarnClient = YarnClient.createYarnClient();
    yarnClient.init(conf);
    yarnClient.start();

    System.out.println("Create YarnClientApplication via YarnClient") ;
    YarnClientApplication app = yarnClient.createApplication();

    System.out.println("Set up the container launch context for the application master") ;
    ContainerLaunchContext amContainer = Records.newRecord(ContainerLaunchContext.class);
    amContainer.setCommands(appOpts.buildAppMasterCommands()) ;

    System.out.println("Setup jar for ApplicationMaster if the jar parameter is set") ;
    if(appOpts.jarFile != null) {
      LocalResource appMasterJar = Records.newRecord(LocalResource.class);
      setupAppMasterJar(conf, appOpts.jarFile, appMasterJar);
      amContainer.setLocalResources(Collections.singletonMap("app.jar", appMasterJar));
    }
    
    System.out.println("Setup the classpath for ApplicationMaster") ;
    Map<String, String> appMasterEnv = new HashMap<String, String>();
    setupAppMasterEnv(appOpts, conf, appMasterEnv);
    amContainer.setEnvironment(appMasterEnv);

    System.out.println("Set up resource type requirements for ApplicationMaster") ;
    Resource resource = Records.newRecord(Resource.class);
    resource.setMemory(256);
    resource.setVirtualCores(1);

    System.out.println("Finally, set-up ApplicationSubmissionContext for the application");
    ApplicationSubmissionContext appContext = app.getApplicationSubmissionContext();
    appContext.setApplicationName(appOpts.appName); // application name
    appContext.setAMContainerSpec(amContainer);
    appContext.setResource(resource);
    appContext.setQueue("default"); // queue 

    // Submit application
    ApplicationId appId = appContext.getApplicationId();
    System.out.println("Submitting application " + appId);
    yarnClient.submitApplication(appContext);

    return new AppClientReporter(yarnClient, appId) ;
  }
}