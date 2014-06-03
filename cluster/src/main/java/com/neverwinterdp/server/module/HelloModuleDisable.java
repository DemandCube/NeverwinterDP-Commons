package com.neverwinterdp.server.module;

import java.util.Map;

import com.neverwinterdp.server.service.HelloService;

@ModuleConfig(name = "HelloModuleDisable", autoInstall = false, autostart = false) 
public class HelloModuleDisable extends ServiceModule {
  protected void configure(Map<String, String> properties) {  
    properties.put("hello", "hello property") ;
    properties.put("hello:hello", "hello map property") ;
    
    bind("HelloService", HelloService.class); 
    bind("HelloServiceInstance", new HelloService()); ;
  }
}