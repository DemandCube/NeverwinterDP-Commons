package com.neverwinterdp.server.module;

import java.util.Map;

import com.neverwinterdp.server.yara.YaraServerService;

@ModuleConfig(name = "YaraServer", autostart = false, autoInstall=false)
public class YaraServerModule extends ServiceModule {
  protected void configure(Map<String, String> properties) {
    bindService(YaraServerService.class);
  }
}