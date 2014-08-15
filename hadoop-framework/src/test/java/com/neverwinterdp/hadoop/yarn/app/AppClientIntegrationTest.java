package com.neverwinterdp.hadoop.yarn.app;

import org.junit.Test;

import com.neverwinterdp.hadoop.yarn.app.AppClient;
import com.neverwinterdp.hadoop.yarn.app.AppClientMonitor;

public class AppClientIntegrationTest  {

  @Test
  public void testIntegrationAppClient() throws Exception {
    String[] args = { 
        "--app-home", "/tmp/app/hello",
        "--app-home-local", "./build/hello"  ,
        "--app-name", "Hello_Yarn",
        
        "--app-container-manager", "com.neverwinterdp.hadoop.yarn.sample.HelloAppContainerManger",
        "--conf:fs.default.name=hdfs://hadoop:9000",
        "--conf:dfs.replication=1",
        "--conf:yarn.resourcemanager.scheduler.address=hadoop:8030",
        "--conf:yarn.resourcemanager.address=hadoop:8032"
      } ;
      
      AppClient appClient = new AppClient() ;
      AppClientMonitor reporter = appClient.run(args);
      System.out.println("AppClient.run() done!!!");
      reporter.monitor(); 
      reporter.report(System.out);
  }
}