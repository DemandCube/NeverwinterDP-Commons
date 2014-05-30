package com.neverwinterdp.server.cluster.shell;

import static org.junit.Assert.*;

import org.junit.Test;

public class BuiltinCommandGroupUnitTest {
  @Test
  public void testAssertCommand() {
    Shell shell = new Shell() ;
    shell.execute(
      ":echo  \"line exp1 exp2 2 --line exp3 exp4 2\""
    );
    shell.execute(
        ":assert --last-command-output" +
        "  --line .*exp1.* .*exp2.* 1" +
        "  --line .*exp3.* .*exp4.* 1"
    );
    assertFalse(shell.getShellContext().getExecuteContext().hasError()) ;
  }
}
