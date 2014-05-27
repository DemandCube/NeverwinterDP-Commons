package com.neverwinterdp.util.monitor;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class MonitorRegistry {
  private String host ;
  private String application  ;
  private String uriPrefix ;
  private MetricRegistry registry = new MetricRegistry();
  
  public MonitorRegistry() {} 
  
  public MonitorRegistry(String host, String application) {
    this.host = host ;
    this.application = application ;
    uriPrefix = "monitor:" + application + "://" + host  ;
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
    b.append(uriPrefix) ;
    for(int i = 0; i < name.length; i++) {
      b.append("/").append(name[i]) ;
    }
    return b.toString() ;
  }
}