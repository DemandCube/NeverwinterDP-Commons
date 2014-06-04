package com.neverwinterdp.server.shell;

import static org.junit.Assert.*;

import org.junit.Test;

import com.beust.jcommander.JCommander;
import com.neverwinterdp.server.shell.Shell;

public class ModuleCommandGroupUnitTest {
  @Test
  public void testAssertCommand() {
    Shell shell = new Shell() ;
    shell.execute(
      ":echo  \"This is a test\""
    );
    ModuleCommandGroup.Install install = new ModuleCommandGroup.Install() ;
    String[] args = {
        "--autostart",
        "-Pproperty1=property1", "-Pproperty2=property2", 
        "-Psub:property1=property1",
        "HelloModule"
    } ;
    new JCommander(install, args) ;
    assertTrue(install.autostart) ;
    assertEquals("HelloModule", install.modules.get(0)) ;
    System.out.println(install.properties);
  }
}
