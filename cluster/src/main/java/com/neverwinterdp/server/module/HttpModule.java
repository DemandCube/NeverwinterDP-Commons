package com.neverwinterdp.server.module;

import java.util.Map;

import com.neverwinterdp.server.service.HttpService;

@ModuleConfig(name = "Http", autoInstall = false, autostart = false) 
public class HttpModule extends ServiceModule {
  protected void configure(Map<String, String> properties) {  
    bindService(HttpService.class); 
  }
}