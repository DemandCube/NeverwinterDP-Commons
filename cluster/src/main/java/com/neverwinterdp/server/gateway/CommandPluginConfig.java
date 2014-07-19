package com.neverwinterdp.server.gateway;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@interface PluginConfig {
  String name();
}