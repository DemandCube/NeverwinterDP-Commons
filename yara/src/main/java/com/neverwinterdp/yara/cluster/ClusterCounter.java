package com.neverwinterdp.yara.cluster;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.neverwinterdp.yara.Counter;

public class ClusterCounter {
  private boolean modified = false ;
  private Counter counter = new Counter() ;
  private Map<String, Counter> counters = new ConcurrentHashMap<String, Counter>() ;
  
  public Counter getCounter() {
    updateIfModified() ;
    return this.counter ; 
  }
  
  public void update(String name, Counter counter) {
    modified = true ;
    counters.put(name, counter) ;
  }
  
  public Map<String, Counter> getCounters() { return this.counters ; }

  private void updateIfModified() {
    if(!modified) return ;
    counter = Counter.combine(counters.values()) ;
    modified = false ;
  }
}
