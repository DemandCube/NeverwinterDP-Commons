package com.neverwinterdp.yara.cluster;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.neverwinterdp.yara.Timer;

public class ClusterTimer {
  private boolean modified = false;
  private Timer timer = new Timer() ;
  private Map<String, Timer> timers = new ConcurrentHashMap<String, Timer>() ;

  public Timer getTimer() { 
    updateIfModified() ;
    return this.timer ; 
  }
  
  public void update(String name, Timer timer) {
    timers.put(name, timer) ;
    modified = true ;
  }
 
  public Map<String, Timer> getTimers() { return this.timers ; }
  
  private void updateIfModified() {
    if(!modified) return ;
    this.timer = Timer.combine(timers.values()) ;
    modified = false ;
  }
}