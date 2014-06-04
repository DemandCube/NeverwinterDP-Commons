package com.neverwinterdp.server.monitor;

import java.io.Serializable;

import com.neverwinterdp.util.monitor.snapshot.MetricRegistrySnapshot;

public class ComponentMonitorSnapshot implements Serializable {
  private String host ;
  private String application ;
  private String domain ;
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
  public String getDomain() {
    return domain;
  }
  public void setDomain(String domain) {
    this.domain = domain;
  }
  public MetricRegistrySnapshot getRegistry() {
    return registry;
  }
  public void setRegistry(MetricRegistrySnapshot registry) {
    this.registry = registry;
  }
}