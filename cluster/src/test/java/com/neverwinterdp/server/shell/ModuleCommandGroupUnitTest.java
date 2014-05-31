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
        "-Mproperty1=property1", "-Mproperty2=property2", 
        "-Msub:property1=property1",
        "HelloModule"
    } ;
    new JCommander(install, args) ;
    assertTrue(install.autostart) ;
    assertEquals("HelloModule", install.modules.get(0)) ;
    System.out.println(install.properties);
  }
}
