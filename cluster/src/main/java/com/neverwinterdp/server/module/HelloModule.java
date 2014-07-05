package com.neverwinterdp.server.module;

import java.util.Map;

import com.neverwinterdp.server.service.HelloService;

@ModuleConfig(name = "HelloModule", autoInstall = true, autostart = true) 
public class HelloModule extends ServiceModule {
  protected void configure(Map<String, String> properties) {  
    properties.put("hello", "hello property") ;
    properties.put("hello:hello", "hello map property") ;
    
    bindService(HelloService.class); 
    bind("HelloServiceInstance", new HelloService()); ;
  }
}