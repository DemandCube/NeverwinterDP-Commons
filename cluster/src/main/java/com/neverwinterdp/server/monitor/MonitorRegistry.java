package com.neverwinterdp.server.monitor;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.neverwinterdp.server.service.Service;

/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class MonitorRegistry {
  private String host ;
  private String application  ;
  private MetricRegistry registry = new MetricRegistry();
  
  public MonitorRegistry() {} 
  
  public MonitorRegistry(String host, String application) {
    this.host = host ;
    this.application = application ;
  }
  
  public String getHost() { return this.host ; }
  
  public String getApplication() { return this.application ; }
  
  public MetricRegistry getRegistry() { return this.registry ; }
  
  public Counter counter(String ... name) {
    return registry.counter(name(name)) ;
  }
  
  public Timer timer(String ... name) {
    return registry.timer(name(name)) ;
  }
  
  private String name(String ... name) {
    StringBuilder b = new StringBuilder() ;
    for(int i = 0; i < name.length; i++) {
      if(i > 0) b.append("/") ;
      b.append(name[i]) ;
    }
    return b.toString() ;
  }
  
  public ComponentMonitorRegistry createComponentMonitorRegistry(Service service) {
    ComponentMonitorRegistry reg = new ComponentMonitorRegistry(service, this) ;
    return reg;
  }
}