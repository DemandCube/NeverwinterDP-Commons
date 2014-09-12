package com.neverwinterdp.yara;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MetricRegistry implements Serializable {
  private String name ;
  private ConcurrentMap<String, Counter> counters = new ConcurrentHashMap<String, Counter>();
  private ConcurrentMap<String, Timer>   timers = new ConcurrentHashMap<String, Timer>();

  public MetricRegistry() { }
  
  public MetricRegistry(String name) { 
    this.name = name ;
  }
  
  public String getName() { return this.name ; }

  public Map<String, Counter> getCounters() { return this.counters ; }
  
  public Counter getCounter(String name) {
    Counter counter = counters.get(name) ;
    if(counter != null) return counter ;
    synchronized(counters) {
      counter = counters.get(name) ;
      if(counter != null) return counter ;
      counter = new Counter() ;
      counters.put(name, counter) ;
    }
    return counter ;
  }
  
  public Map<String, Timer> getTimers() { return this.timers ; }
  
  public Timer getTimer(String name) {
    Timer timer = timers.get(name) ;
    if(timer != null) return timer ;
    synchronized(timers) {
      timer = timers.get(name) ;
      if(timer != null) return timer ;
      timer = new Timer() ;
      timers.put(name, timer) ;
    }
    return timer ;
  }
}
