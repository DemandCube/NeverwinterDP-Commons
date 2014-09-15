package com.neverwinterdp.yara.snapshot;

import java.util.HashMap;
import java.util.Map;

import com.neverwinterdp.yara.cluster.ClusterCounter;
import com.neverwinterdp.yara.cluster.ClusterMetricRegistry;
import com.neverwinterdp.yara.cluster.ClusterTimer;

public class ClusterMetricRegistrySnapshot {
  private Map<String, ClusterCounterSnapshot> counters = new HashMap<String, ClusterCounterSnapshot>();
  private Map<String, ClusterTimerSnapshot> timers = new HashMap<String, ClusterTimerSnapshot>();

  public ClusterMetricRegistrySnapshot() { }
  
  public ClusterMetricRegistrySnapshot(ClusterMetricRegistry registry) {
    for(Map.Entry<String, ClusterCounter> entry : registry.getCounters().entrySet()) {
      counters.put(entry.getKey(), new ClusterCounterSnapshot(entry.getValue())) ;
    }
    
    for(Map.Entry<String, ClusterTimer> entry : registry.getTimers().entrySet()) {
      timers.put(entry.getKey(), new ClusterTimerSnapshot(entry.getValue())) ;
    }
  }

  public Map<String, ClusterCounterSnapshot> getCounters() { return counters; }

  public void setCounters(Map<String, ClusterCounterSnapshot> counters) { this.counters = counters; }

  public Map<String, ClusterTimerSnapshot> getTimers() { return timers; }

  public void setTimers(Map<String, ClusterTimerSnapshot> timers) { this.timers = timers; }
}
