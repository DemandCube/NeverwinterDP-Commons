package com.neverwinterdp.server.module;

import java.util.Map;

import com.neverwinterdp.server.gateway.http.HttpGatewayService;

@ModuleConfig(name = "HttpGateway", autoInstall = false, autostart = false) 
public class GatewayModule extends ServiceModule {
  protected void configure(Map<String, String> properties) {  
    bindService(HttpGatewayService.class); 
  }
}