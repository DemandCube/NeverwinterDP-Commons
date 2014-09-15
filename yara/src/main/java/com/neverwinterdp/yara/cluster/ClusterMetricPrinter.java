package com.neverwinterdp.yara.cluster;

import java.io.IOException;
import java.util.Map;

import com.neverwinterdp.util.text.StringUtil;
import com.neverwinterdp.yara.Counter;
import com.neverwinterdp.yara.MetricPrinter;
import com.neverwinterdp.yara.Timer;

public class ClusterMetricPrinter {
  private Appendable out = System.out ;
  
  public ClusterMetricPrinter() { } 
  
  public ClusterMetricPrinter(Appendable out) { 
    this.out = out ;
  }
  
  public void print(ClusterMetricRegistry registry) throws IOException {
    printCounters(registry.getCounters()) ;
    printTimers(registry.getTimers()) ;
  }
  
  public void printCounters(Map<String, ClusterCounter> clusterCounters) throws IOException {
    MetricPrinter.CounterPrinter tPrinter = new MetricPrinter.CounterPrinter(out) ;
    String[] keys = StringUtil.toSortedArray(clusterCounters.keySet()) ;
    for(String key : keys) {
      ClusterCounter clusterCounter = clusterCounters.get(key) ;
      tPrinter.print(key, clusterCounter.getCounter());
      Map<String, Counter> counters = clusterCounter.getCounters() ;
      String[] serverNames = StringUtil.toSortedArray(counters.keySet()) ;
      for(String serverName : serverNames) {
        tPrinter.print(" - " + serverName, counters.get(serverName));
      }
    }
    tPrinter.flush(); 
  }
  
  public void printTimers(Map<String, ClusterTimer> clusterTimers) throws IOException {
    MetricPrinter.TimerPrinter tPrinter = new MetricPrinter.TimerPrinter(out) ;
    String[] keys = StringUtil.toSortedArray(clusterTimers.keySet()) ;
    for(String key : keys) {
      ClusterTimer clusterTimer = clusterTimers.get(key) ;
      tPrinter.print(key, clusterTimer.getTimer());
      Map<String, Timer> timers = clusterTimer.getTimers() ;
      String[] serverNames = StringUtil.toSortedArray(timers.keySet()) ;
      for(String serverName : serverNames) {
        tPrinter.print(" - " + serverName, timers.get(serverName));
      }
    }
    tPrinter.flush(); 
  }
}
