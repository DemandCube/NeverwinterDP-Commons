package com.neverwinterdp.hadoop.yarn;

import org.junit.Test;

import com.beust.jcommander.JCommander;

public class AppOptionsUnitTest {
  @Test
  public void test() {
    String[] args = {
      "--conf:key=value"
    };
    
    AppOptions options = new AppOptions() ; 
    new JCommander(options, args) ;
    System.out.println("key = " + options.conf.get("key"));
  }
}