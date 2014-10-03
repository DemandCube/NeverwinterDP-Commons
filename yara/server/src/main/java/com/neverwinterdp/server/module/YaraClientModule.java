package com.neverwinterdp.server.module;

import java.util.Map;

import com.neverwinterdp.server.yara.YaraClientService;

@ModuleConfig(name = "YaraClient", autostart = false, autoInstall=false)
public class YaraClientModule extends ServiceModule {
  protected void configure(Map<String, String> properties) {
    bindService(YaraClientService.class);
  }
}