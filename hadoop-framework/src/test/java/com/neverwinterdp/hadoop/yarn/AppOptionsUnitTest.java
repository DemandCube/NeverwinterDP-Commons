package com.neverwinterdp.hadoop.yarn;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.beust.jcommander.JCommander;

public class AppOptionsUnitTest {
  @Test
  public void test() {
    String[] args = {
      "--cmd:echo=\"echo this is a test\""
    };
    
    AppOptions options = new AppOptions() ; 
    new JCommander(options, args) ;
    System.out.println("echo = " + options.cmd.get("echo"));
    assertEquals("echo this is a test", options.getCommand("echo")) ;
  }
}
