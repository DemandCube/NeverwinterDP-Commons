package com.neverwinterdp.server.service;


public class HelloServiceModule extends ServiceModule {
  @Override
  protected void configure() {  
    bind("HelloService", HelloService.class); 
    bind("HelloServiceInstance", new HelloService()); ;
  }
}