package com.neverwinterdp.server.shell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.neverwinterdp.server.Server;
import com.neverwinterdp.util.FileUtil;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class ShellUnitTest {
  static {
    System.setProperty("app.dir", "build/cluster") ;
    System.setProperty("log4j.configuration", "file:src/test/resources/log4j.properties") ;
  }
  
  @Test
  public void testParseScript() throws Exception {
    Shell shell = new Shell() ;
    String script = 
        "#this is a comment line" + "\n" +
        ":echo \"this is a test\""  + "\n" +
        "#this is a comment line" + "\n" +
        "module install \\" + "\n" +
        "  --autostart \\"  + "\n" +
        "  -Pproperty=value -Pproperty1=value1 \\"  + "\n" +
        "  -Pcustom:property=value -Pcustom:property1=value1 \\"  + "\n" +
        "  --module HelloModule"  + "\n" +
        ":echo \"this is a test\"" ;
    String[] lines = shell.parseScript(script) ;
    for(String line : lines) {
      System.out.println(line);
    }
  }
  
  @Test
  public void testCommand() throws Exception {
    FileUtil.removeIfExist("build/cluster", false);
    Server[] instance = create(2) ;
    System.out.println("\n\n\n");
    Shell shell = new Shell() ;
    shell.execute(":connect 127.0.0.1:5701");
    
    shell.execute(":set shell=test");
    shell.execute(":set host=127.0.0.1");
    shell.execute(":set --type int port=5701");
    shell.execute(":set --type int numOfServer=" + instance.length);
    assertEquals(5701, (int)shell.getShellContext().getVariables().get("port")) ;
    assertEquals("127.0.0.1", (String)shell.getShellContext().getVariables().get("host")) ;
    assertFalse(shell.getShellContext().getExecuteContext().hasError()) ;
   
    shell.execute(":echo \":echo This is an echo text!!!\"");
    shell.execute(":echo \":echo host = $host and port = $port\"");
    
    shell.execute("server ping");
    shell.execute("server ping --member-role master");
    shell.execute("server registration");
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
    Server[] instance = create(2) ;
    String firstMember = instance[0].getClusterService().getMember().getMemberName() ;
    Shell shell = new Shell() ;
    String SCRIPT = 
        ":echo \"===================START TEST=====================\"" + "\n" +
        ":set firstMember=" + firstMember + "\n" +
        ":connect --member $firstMember" + "\n" +
        "module list --type available" + "\n" +
        "server registration" + "\n" +
        ":echo \"Start Install HelloModuleDisable\"" + "\n" +
        "module install " +
        "  --member-name $firstMember --autostart " +
        "  -Phello:install=from-install " +
        "  --module  HelloModuleDisable" + "\n" +
        "module list --type installed" +"\n" +
        ":echo \"Finish Install HelloModuleDisable\"" + "\n" +
        ":sleep 2000" +"\n" + 
        "server registration" + "\n" +
        "module uninstall --module HelloModuleDisable" + "\n" +
        "module list --type available" + "\n" +
        "server registration" + "\n" +
        "server metric --type timer --filter * " + "\n" +
        ":echo \"===================END TEST=====================\"" + "\n" +
        "" ;
    shell.executeScript(SCRIPT);
    shell.close() ; 
    close(instance) ;
  }
  
  Server[] create(int numOfInstance) throws Exception {
    String[] args = {
      "-Pserver.group=NeverwinterDP", "-Pserver.name=test-builder", "-Pserver.roles=master"
    };
    Server[] instance = new Server[numOfInstance] ;
    for(int i = 0; i < instance.length; i++) {
      instance[i] = Server.create(args);  
    }
    Thread.sleep(2000);
    return instance ;
  }
  
  void close(Server[] instance) throws Exception {
    for(int i = 0; i < instance.length; i++) {
      instance[i].destroy() ;
    }
  }
}