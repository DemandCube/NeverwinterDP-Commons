package com.neverwinterdp.util.monitor;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.neverwinterdp.util.monitor.snapshot.MetricRegistrySnapshot;

/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class ApplicationMonitor {
  static ObjectMapper mapper ;
  static {
    mapper = new ObjectMapper() ; 
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.MILLISECONDS, false));
  }
  
  private String host ;
  private String application  ;
  private MetricRegistry registry = new MetricRegistry();
  
  public ApplicationMonitor() {
    this.host = "127.0.0.1" ;
    this.application = "application" ;
  } 
  
  public ApplicationMonitor(String host, String application) {
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
  
  public ComponentMonitor createComponentMonitor(Class<?> type) {
    ComponentMonitor reg = new ComponentMonitor(name(type.getSimpleName()), this) ;
    return reg;
  }
  
  public ComponentMonitor createComponentMonitor(String ... name) {
    ComponentMonitor reg = new ComponentMonitor(name(name), this) ;
    return reg;
  }
  
  public ApplicationMonitorSnapshot snapshot() throws IOException {
    String json = mapper.writeValueAsString(this) ;
    ApplicationMonitorSnapshot snapshot = 
        mapper.readValue(new StringReader(json), ApplicationMonitorSnapshot.class) ;
    return snapshot ;
  }
  
  public String toJSON() throws JsonProcessingException {
    return mapper.writeValueAsString(this) ;
  }
}