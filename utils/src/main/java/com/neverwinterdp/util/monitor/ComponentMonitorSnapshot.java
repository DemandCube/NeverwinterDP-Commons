package com.neverwinterdp.util.monitor;

import java.io.Serializable;

import com.neverwinterdp.util.monitor.snapshot.MetricRegistrySnapshot;

public class ComponentMonitorSnapshot implements Serializable {
  private String host ;
  private String application ;
  private String component ;
  private MetricRegistrySnapshot registry ;
  
  public String getHost() {
    return host;
  }
  public void setHost(String host) {
    this.host = host;
  }
  public String getApplication() {
    return application;
  }
  public void setApplication(String application) {
    this.application = application;
  }
  public String getComponent() {
    return component;
  }
  public void setComponent(String name) {
    this.component = name;
  }
  public MetricRegistrySnapshot getRegistry() {
    return registry;
  }
  public void setRegistry(MetricRegistrySnapshot registry) {
    this.registry = registry;
  }
}