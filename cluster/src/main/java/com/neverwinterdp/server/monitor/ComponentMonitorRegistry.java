package com.neverwinterdp.server.monitor;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;
import com.neverwinterdp.server.service.Service;
import com.neverwinterdp.server.service.ServiceRegistration;
import com.neverwinterdp.util.monitor.snapshot.MetricRegistrySnapshot;

public class ComponentMonitorRegistry {
  private String domainName ;
  private MonitorRegistry registry ;
  
  public ComponentMonitorRegistry(String domainName, MonitorRegistry registry) {
    this.domainName = domainName ;
    this.registry = registry ;
  }
  
  public ComponentMonitorRegistry(Service service, MonitorRegistry registry) {
    ServiceRegistration serviceRegistration = service.getServiceRegistration() ;
    this.domainName = serviceRegistration.getModule() + "/" + serviceRegistration.getServiceId();
    this.registry = registry ;
  }
  
  public Counter counter(String name) {
    return registry.counter(domainName, name) ;
  }
  
  public Timer timer(String name) {
    return registry.timer(domainName, name) ;
  }
  
  public void reset() {
    //TODO
  }
  
  public ComponentMonitorSnapshot getComponentMonitorSnapshot() {
    MetricRegistrySnapshot regSnapshot = new MetricRegistrySnapshot(domainName, registry.getRegistry()) ; 
    ComponentMonitorSnapshot snapshot = new ComponentMonitorSnapshot() ;
    snapshot.setHost(registry.getHost());
    snapshot.setApplication(registry.getApplication());
    snapshot.setDomain(domainName);
    snapshot.setRegistry(regSnapshot);
    return snapshot ;
  }
}
