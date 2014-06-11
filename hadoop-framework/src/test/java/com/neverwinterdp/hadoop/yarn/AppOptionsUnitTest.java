package com.neverwinterdp.hadoop.yarn;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.beust.jcommander.JCommander;

public class AppOptionsUnitTest {
  @Test
  public void test() {
    String[] args = {
      "--command", "java -Dsys.prop=prop package.Hello1 -param1 -param2 arg1",
      "--command", "java -Dsys.prop=prop package.Hello2 -param1 -param2 arg1"
    };
    
    AppOptions options = new AppOptions() ; 
    new JCommander(options, args) ;
    assertEquals(2, options.commands.size()) ;
  }
}
