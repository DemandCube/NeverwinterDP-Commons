package com.neverwinterdp.util.monitor.snapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class MetricRegistrySnapshot implements Serializable {
  private String                       version;
  private Map<String, Object>          gauges;
  private Map<String, CounterSnapshot> counters;
  private Map<String, Object>          histograms;
  private Map<String, Object>          meters;
  private Map<String, TimerSnapshot>   timers;

  public MetricRegistrySnapshot() {
    
  }
  
  public MetricRegistrySnapshot(String prefix, MetricRegistry registry) {
    for(Map.Entry<String, Metric> entry : registry.getMetrics().entrySet()) {
      String key = entry.getKey() ;
      if(prefix != null && !key.startsWith(prefix)) continue ;
      Metric metric = entry.getValue() ;
      if(metric instanceof Counter) add(key, (Counter) metric) ;
      if(metric instanceof Timer) add(key, (Timer) metric) ;
      //TODO: copy the other metric and create unit test
    }
  }
  public String getVersion() { return version; }
  public void setVersion(String version) { this.version = version; }

  public Map<String, Object> getGauges() { return gauges; }
  public void setGauges(Map<String, Object> gauges) { this.gauges = gauges; }

  public Map<String, CounterSnapshot> getCounters() { return counters; }
  public void setCounters(Map<String, CounterSnapshot> counters) { this.counters = counters; }

  public Map<String, Object> getHistograms() { return histograms; }
  public void setHistograms(Map<String, Object> histograms) {
    this.histograms = histograms;
  }

  public Map<String, Object> getMeters() { return meters; }
  public void setMeters(Map<String, Object> meters) {
    this.meters = meters;
  }

  public Map<String, TimerSnapshot> getTimers() { return timers; }
  public void setTimers(Map<String, TimerSnapshot> timers) {
    this.timers = timers;
  }
  
  public CounterSnapshot counter(String name) {
    return counters.get(name) ;
  }
  
  public TimerSnapshot timer(String name) {
    return timers.get(name) ;
  }

  //TODO: to implement
  public void merge(MetricRegistrySnapshot other) {
    
  }
  
  public void add(String key, Counter counter) {
    if(counters == null) counters = new HashMap<String, CounterSnapshot>() ;
    counters.put(key, new CounterSnapshot(counter)) ;
  }
  
  public void add(String key, Timer timer) {
    if(timers == null) timers = new HashMap<String, TimerSnapshot>() ;
    timers.put(key, new TimerSnapshot(timer)) ;
  }
  
  public CounterSnapshot findCounter(String exp) {
    return find(counters, exp) ;
  }
  
  public Map<String, CounterSnapshot> findCounters(String exp) {
    return findAll(counters, exp) ;
  }
  
  public TimerSnapshot findTimer(String exp) {
    return find(timers, exp) ;
  }
  
  public Map<String, TimerSnapshot> findTimers(String exp) {
    return findAll(timers, exp) ;
  }
  
  private <T> T find(Map<String, T> map, String exp) {
    exp.replace("*", ".*") ;
    Pattern pattern = Pattern.compile(exp) ;
    for(Map.Entry<String, T> entry : map.entrySet()) {
      String key = entry.getKey() ;
      if(pattern.matcher(key).matches()) return entry.getValue() ;
    }
    return null ;
  }
  
  private <T> Map<String, T> findAll(Map<String, T> map, String exp) {
    exp = exp.replace("*", ".*") ;
    Map<String, T> holder = new HashMap<String, T>() ;
    Pattern pattern = Pattern.compile(exp) ;
    for(Map.Entry<String, T> entry : map.entrySet()) {
      String key = entry.getKey() ;
      if(pattern.matcher(key).matches()) holder.put(key, entry.getValue()) ;
    }
    return holder ;
  }
}
