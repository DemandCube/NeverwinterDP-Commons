package com.neverwinterdp.server.yara;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.shell.Shell;
import com.neverwinterdp.util.FileUtil;

public class YaraServerModuleUnitTest {
  static {
    System.setProperty("app.dir", "build/cluster") ;
    System.setProperty("app.config.dir", "build/cluster") ;
    System.setProperty("log4j.configuration", "file:src/test/resources/log4j.properties") ;
  }
  
  static Server  master ;
  static Server[]  worker ;
  static Shell   shell ;
  
  @BeforeClass 
  static public void setup() throws Exception {
    FileUtil.removeIfExist("build/cluster", false);
    master = Server.create("-Pserver.name=master", "-Pserver.roles=master") ;
    worker = new Server[2] ;
    for(int i = 0; i < worker.length; i++) {
      worker[i] = Server.create("-Pserver.name=worker" + i, "-Pserver.roles=worker") ;
    }
    shell = new Shell() ;
    shell.getShellContext().connect();
    shell.execute("module list --type available");
    Thread.sleep(1000);
  }
  
  @AfterClass
  static public void teardown() {
    shell.close() ; 
    for(int i = 0; i < worker.length; i++) {
      worker[i].destroy(); 
    }
    master.destroy();
  }
  
  @Test
  public void testYaraMetricService() throws Exception {
    install() ;
    for(int i = 0; i < 2; i++) {
      shell.exec("server metric"); 
      Thread.sleep(5000);
    }
    shell.exec("yara snapshot --member-role master");
    uninstall() ;
  }
  
  public void install() throws InterruptedException {
    String installScript =
        "module install " + 
        " -Pmodule.data.drop=true" +
        " -Pyara:rpc.port=8463" +
        " --member-role master --autostart --module YaraServer \n" +
        
        "module install " + 
        " -Pmodule.data.drop=true" +
        " -Pyara:rpc.host=127.0.0.1" +
        " -Pyara:rpc.port=8463" +
        " --member-role worker --autostart --module YaraClient --timeout 10000" ;
    shell.executeScript(installScript);
    Thread.sleep(1000);
  }
  
  public void uninstall() {
    String uninstallScript = 
      "module uninstall --member-role worker --timeout 20000 --module YaraClient \n" +
      "module uninstall --member-role master --timeout 20000 --module YaraServer";
    shell.executeScript(uninstallScript);
  }
}
