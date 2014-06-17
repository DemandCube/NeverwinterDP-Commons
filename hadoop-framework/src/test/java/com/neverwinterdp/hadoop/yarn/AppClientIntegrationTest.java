package com.neverwinterdp.hadoop.yarn;

import org.junit.Test;

public class AppClientIntegrationTest  {

  @Test
  public void testAppClient() throws Exception {
    String[] args = { 
        "--app-home", "/tmp/neverwinterdp/libs/",
        "--upload-app", "build/libs" ,
        "--app-name", "Hello Yarn",
        "--container-manager", "com.neverwinterdp.hadoop.yarn.hello.HelloAppContainerManger",
        "--conf:fs.default.name=hdfs://hadoop:9000",
        "--conf:dfs.replication=1",
        "--conf:yarn.resourcemanager.scheduler.address=hadoop:8030",
        "--conf:yarn.resourcemanager.address=hadoop:8032"
      } ;
      
      AppClient appClient = new AppClient() ;
      AppClientMonitor reporter = appClient.run(args);
      reporter.monitor(); 
      reporter.report(System.out);
  }
}