package com.neverwinterdp.server.shell;

import static org.junit.Assert.*;

import org.junit.Test;

import com.neverwinterdp.server.shell.Shell;

public class BuiltinCommandGroupUnitTest {
  @Test
  public void testAssertCommand() {
    Shell shell = new Shell() ;
    shell.execute(":echo  \"line exp1 exp2 2 --line exp3 exp4 2\"");
    
    shell.execute(
      ":assert --last-command-output" +
      "  --line .*exp1.* .*exp2.* 1" +
      "  --line .*exp3.* .*exp4.* 1"
    );
    assertFalse(shell.getShellContext().getExecuteContext().hasError()) ;
    
    shell.execute(":set shell=test");
    shell.execute(":set host=127.0.0.1");
    shell.execute(":set --type int port=5701");
    shell.execute(":set --type int numOfServer=1");
    assertEquals(5701, (int)shell.getShellContext().getVariables().get("port")) ;
    assertEquals("127.0.0.1", (String)shell.getShellContext().getVariables().get("host")) ;
  }
}
