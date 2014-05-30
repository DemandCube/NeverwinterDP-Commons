package com.neverwinterdp.server.service;


public class HelloModule extends ServiceModule {
  @Override
  protected void configure() {  
    bind("HelloService", HelloService.class); 
    bind("HelloServiceInstance", new HelloService()); ;
  }
}