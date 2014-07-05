package com.neverwinterdp.util.monitor.snapshot;

import java.io.Serializable;
import java.util.List;

import com.neverwinterdp.util.text.TabularPrinter;


/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class ApplicationMonitorSnapshot implements Serializable {
  private String                 host;
  private String                 application;
  private MetricRegistrySnapshot registry = new MetricRegistrySnapshot();

  public String getHost() { return host; }
  public void setHost(String host) { 
    this.host = host; 
  }

  public String getApplication() { return application; }
  public void setApplication(String application) { 
    this.application = application; 
  }

  public MetricRegistrySnapshot getRegistry() { return registry; }
  public void setRegistry(MetricRegistrySnapshot registry) { this.registry = registry;}
  
  public CounterSnapshot counter(String ... name) {
    return registry.counter(name(name)) ;
  }
  
  public TimerSnapshot timer(String ... name) {
    return registry.timer(name(name)) ;
  }
  
  public void filter(String exp) { registry.filter(exp); }
  
  private String name(String ... name) {
    StringBuilder b = new StringBuilder() ;
    for(int i = 0; i < name.length; i++) {
      if(i > 0) b.append("/") ;
      b.append(name[i]) ;
    }
    return b.toString() ;
  }
}
