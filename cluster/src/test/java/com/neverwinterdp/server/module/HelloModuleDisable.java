package com.neverwinterdp.server.module;

import java.util.Map;

import com.neverwinterdp.server.service.hello.HelloService;

/**
 * @author Tuan
 * This module class is used to configure the available services for the module. The default parameters and the default 
 * action such auto install when the module is installed, auto start all the services in the module when the module is
 * installed
 */
@ModuleConfig(name = "HelloModuleDisable", autoInstall = false, autostart = false) 
public class HelloModuleDisable extends ServiceModule {
  protected void configure(Map<String, String> properties) {  
    properties.put("hello", "hello property") ;
    properties.put("hello:hello", "hello map property") ;
    
    //register the same HelloService with the different service id
    bind("HelloService", HelloService.class); 
    bind("HelloServiceInstance", new HelloService()); ;
  }
}