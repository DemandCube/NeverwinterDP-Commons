package com.neverwinterdp.server.cluster.shell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Properties;

import org.junit.Test;

import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.service.HelloModule;
import com.neverwinterdp.util.FileUtil;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class ShellUnitTest {
  static {
    System.setProperty("app.dir", "build/cluster") ;
    System.setProperty("log4j.configuration", "file:src/main/resources/log4j.properties") ;
  }
  
  @Test
  public void testCommand() throws Exception {
    FileUtil.removeIfExist("build/cluster", false);
    Server[] instance = create(2, true, true) ;
    System.out.println("\n\n\n");
    Shell shell = new Shell() ;
    shell.execute("cluster connect --members 127.0.0.1:5701 127.0.0.1:5702 ");
    
    shell.execute(":set shell=test");
    shell.execute(":set host=127.0.0.1");
    shell.execute(":set --type int port=5701");
    shell.execute(":set --type int numOfServer=" + instance.length);
    assertEquals(5701, (int)shell.getShellContext().getVariables().get("port")) ;
    assertEquals("127.0.0.1", (String)shell.getShellContext().getVariables().get("host")) ;
    assertFalse(shell.getShellContext().getExecuteContext().hasError()) ;
   
    shell.execute(":echo \":echo This is an echo text!!!\"");
    shell.execute(":echo \":echo host = $host and port = $port\"");
    
    shell.execute("cluster ping");
    shell.execute("cluster registration");
    shell.execute(
     ":assert" +
     "  --last-command-output " + 
     "  --line .*HelloModule.* .*HelloService\\s.* $numOfServer" +
     "  --line .*State.* .*RUNNING.* $numOfServer" 
    );
    assertFalse(shell.getShellContext().getExecuteContext().hasError()) ;
    
    System.out.println("\n\n\n");
    close(instance) ;
  }
  
  @Test
  public void testInstall() throws Exception {
    FileUtil.removeIfExist("build/cluster", false);
    Server[] instance = create(1, false, false) ;
    System.out.println("\n\n\n");
    Shell shell = new Shell() ;
    shell.execute("cluster connect --members 127.0.0.1:5701 127.0.0.1:5702 ");
    shell.execute("cluster module --available");
    shell.execute("cluster module --install HelloModule --install-autostart");
    shell.execute("cluster module --installed");
    Thread.sleep(500);
    shell.execute("cluster registration");
    shell.execute("cluster module --uninstall HelloModule");
    shell.execute("cluster module --available");
    shell.execute("cluster registration");
    close(instance) ;
  }
  
  Server[] create(int numOfInstance, boolean install, boolean autostart) throws Exception {
    Properties properties = new Properties() ;
    properties.put("server.group", "NeverwinterDP") ;
    properties.put("server.cluster-framework", "hazelcast") ;
    properties.put("server.roles", "master") ;
    properties.put("server.available-modules", HelloModule.class.getName()) ;
    if(install) {
      properties.put("server.install-modules", HelloModule.class.getName()) ;
      if(autostart) {
        properties.put("server.install-modules-autostart", "true") ;
      }
    }
    
    
    Server[] instance = new Server[numOfInstance] ;
    for(int i = 0; i < instance.length; i++) {
      instance[i] = Server.create(properties) ;  
    }
    Thread.sleep(2000);
    return instance ;
  }
  
  void close(Server[] instance) throws Exception {
    for(int i = 0; i < instance.length; i++) {
      instance[i].exit(0) ;
    }
  }
}