package com.neverwinterdp.util.monitor;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;
import com.neverwinterdp.util.monitor.snapshot.ComponentMonitorSnapshot;
import com.neverwinterdp.util.monitor.snapshot.MetricRegistrySnapshot;

public class ComponentMonitor {
  private String path ;
  private ApplicationMonitor registry ;
  
  public ComponentMonitor(String name, ApplicationMonitor registry) {
    this.path = name ;
    this.registry = registry ;
  }
  
  public Counter counter(String name) {
    return registry.counter(path, name) ;
  }
  
  public Timer timer(String name) {
    return registry.timer(path, name) ;
  }
  
  public void reset() {
    //TODO
  }
  
  public ComponentMonitorSnapshot getComponentMonitorSnapshot() {
    MetricRegistrySnapshot regSnapshot = new MetricRegistrySnapshot(path, registry.getRegistry()) ; 
    ComponentMonitorSnapshot snapshot = new ComponentMonitorSnapshot() ;
    snapshot.setHost(registry.getHost());
    snapshot.setApplication(registry.getApplication());
    snapshot.setComponent(path);
    snapshot.setRegistry(regSnapshot);
    return snapshot ;
  }
}
