package com.neverwinterdp.yara.cluster;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import com.neverwinterdp.yara.Counter;
import com.neverwinterdp.yara.MetricPrinter;
import com.neverwinterdp.yara.Timer;

public class ClusterMetricPrinter {
  private Appendable out = System.out ;
  
  public void print(ClusterMetricRegistry registry) throws IOException {
    printCounters(registry.getCounters()) ;
    printTimers(registry.getTimers()) ;
  }
  
  public void printCounters(Map<String, ClusterCounter> clusterCounters) throws IOException {
    MetricPrinter.CounterPrinter tPrinter = new MetricPrinter.CounterPrinter(out) ;
    Iterator<Map.Entry<String, ClusterCounter>> clusterCounterItr = clusterCounters.entrySet().iterator() ;
    while(clusterCounterItr.hasNext()) {
      Map.Entry<String, ClusterCounter> entry = clusterCounterItr.next() ;
      ClusterCounter clusterCounter = entry.getValue() ;
      tPrinter.print(entry.getKey(), clusterCounter.getCounter());
      Map<String, Counter> timers = clusterCounter.getCounters() ;
      Iterator<Map.Entry<String, Counter>> counterItr = timers.entrySet().iterator() ;
      while(counterItr.hasNext()) {
        Map.Entry<String, Counter> cEntry = counterItr.next() ;
        tPrinter.print(" - " + cEntry.getKey(), cEntry.getValue());
      }
    }
    tPrinter.flush(); 
  }
  
  public void printTimers(Map<String, ClusterTimer> clusterTimers) throws IOException {
    MetricPrinter.TimerPrinter tPrinter = new MetricPrinter.TimerPrinter(out) ;
    Iterator<Map.Entry<String, ClusterTimer>> clusterTimerItr = clusterTimers.entrySet().iterator() ;
    while(clusterTimerItr.hasNext()) {
      Map.Entry<String, ClusterTimer> entry = clusterTimerItr.next() ;
      ClusterTimer clusterTimer = entry.getValue() ;
      tPrinter.print(entry.getKey(), clusterTimer.getTimer());
      Map<String, Timer> timers = clusterTimer.getTimers() ;
      Iterator<Map.Entry<String, Timer>> timerItr = timers.entrySet().iterator() ;
      while(timerItr.hasNext()) {
        Map.Entry<String, Timer> tEntry = timerItr.next() ;
        tPrinter.print(" - " + tEntry.getKey(), tEntry.getValue());
      }
    }
    tPrinter.flush(); 
  }
}
