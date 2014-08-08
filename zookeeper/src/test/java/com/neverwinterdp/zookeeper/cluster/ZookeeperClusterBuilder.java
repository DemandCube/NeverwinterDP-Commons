package com.neverwinterdp.zookeeper.cluster;

import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.shell.Shell;
import com.neverwinterdp.util.FileUtil;

public class ZookeeperClusterBuilder {
  static {
    System.setProperty("app.dir", "build/cluster") ;
    System.setProperty("app.config.dir", "build/cluster") ;
    System.setProperty("log4j.configuration", "file:src/test/resources/log4j.properties") ;
  }
  
  Server  zkServer;
  Shell   shell ;

  public ZookeeperClusterBuilder() throws Exception {
    FileUtil.removeIfExist("build/cluster", false);
    zkServer = Server.create("-Pserver.name=zookeeper", "-Pserver.roles=zookeeper") ;
    
    shell = new Shell() ;
    shell.getShellContext().connect();
    shell.execute("module list --type available");
    Thread.sleep(1000);
  }

  public Shell getShell() { return this.shell ; }
  
  public void destroy() throws Exception {
    shell.close() ; 
    zkServer.destroy();
  }
  
  public void install() throws InterruptedException {
    String installScript =
        "module install " + 
        " -Pmodule.data.drop=true" +
        " -Pzk:clientPort=2181 " +
        " --member-role zookeeper --autostart --module Zookeeper" ;
      shell.executeScript(installScript);
      Thread.sleep(1000);
  }
  
  public void uninstall() {
    String uninstallScript = 
        "module uninstall --member-role zookeeper --timeout 20000 --module Zookeeper";
    shell.executeScript(uninstallScript);
  }
}