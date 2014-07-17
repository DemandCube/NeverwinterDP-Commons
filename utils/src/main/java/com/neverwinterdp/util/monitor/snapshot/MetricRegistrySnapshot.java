package com.neverwinterdp.util.monitor.snapshot;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.neverwinterdp.util.monitor.mergestrategy.MergeFormula;

/**
 * @author Tuan Nguyen
 * @email tuan08@gmail.com
 */
public class MetricRegistrySnapshot implements Serializable {
    private String version;
    private Map<String, Object> gauges;
    private Map<String, CounterSnapshot> counters;
    private Map<String, Object> histograms;
    private Map<String, MeterSnapshot> meters;
    private Map<String, TimerSnapshot> timers;
    private static Map<String, MetricRegistrySnapshot> registriesMap;

    public MetricRegistrySnapshot() {

    }

    public MetricRegistrySnapshot(String prefix, MetricRegistry registry) {
	for (Map.Entry<String, Metric> entry : registry.getMetrics().entrySet()) {
	    String key = entry.getKey();
	    if (prefix != null && !key.startsWith(prefix))
		continue;
	    Metric metric = entry.getValue();
	    if (metric instanceof Counter)
		add(key, (Counter) metric);
	    if (metric instanceof Timer)
		add(key, (Timer) metric);
	    // TODO: copy the other metric and create unit test
	}
    }

	public String getVersion() {
	return version;
    }

    public void setVersion(String version) {
	this.version = version;
    }

    public Map<String, Object> getGauges() {
	return gauges;
    }

    public void setGauges(Map<String, Object> gauges) {
	this.gauges = gauges;
    }

    public Map<String, CounterSnapshot> getCounters() {
	return counters;
    }

    public void setCounters(Map<String, CounterSnapshot> counters) {
	this.counters = counters;
    }

    public Map<String, Object> getHistograms() {
	return histograms;
    }

    public void setHistograms(Map<String, Object> histograms) {
	this.histograms = histograms;
    }

    public Map<String, MeterSnapshot> getMeters() {
	return meters;
    }

    public void setMeters(Map<String, MeterSnapshot> meters) {
	this.meters = meters;
    }

    public Map<String, TimerSnapshot> getTimers() {
	return timers;
    }

    public void setTimers(Map<String, TimerSnapshot> timers) {
	this.timers = timers;
    }

    public static Map<String, MetricRegistrySnapshot> getRegistriesMap() {

	if (registriesMap == null) {
	    registriesMap = new LinkedHashMap<String, MetricRegistrySnapshot>();
	}
	
	return registriesMap;
    }

    public CounterSnapshot counter(String name) {
	return counters.get(name);
    }

    public MeterSnapshot meter(String name) {
	return meters.get(name);
    }

    public TimerSnapshot timer(String name) {
	return timers.get(name);
    }

    // TODO: to implement
    public void merge(MetricRegistrySnapshot other) {

    }

    public void add(String key, Counter counter) {
	if (counters == null)
	    counters = new HashMap<String, CounterSnapshot>();
	counters.put(key, new CounterSnapshot(counter));
    }

    public void add(String key, Timer timer) {
	if (timers == null)
	    timers = new HashMap<String, TimerSnapshot>();
	timers.put(key, new TimerSnapshot(timer));
    }

    public static MetricRegistrySnapshot convert(MetricRegistry registry) {
	ObjectMapper mapper = new ObjectMapper();
	mapper.enable(SerializationFeature.INDENT_OUTPUT);
	mapper.registerModule(new MetricsModule(TimeUnit.SECONDS,
		TimeUnit.MILLISECONDS, false));
	MetricRegistrySnapshot regSnapshot = null;
	String json;
	try {
	    json = mapper.writeValueAsString(registry);
	    StringReader reader = new StringReader(json);

	    regSnapshot = mapper
		    .readValue(reader, MetricRegistrySnapshot.class);
	} catch (IOException e) {
	    e.printStackTrace();

	}
	Map<String, TimerSnapshot> countersList = regSnapshot.getTimers();
	for (String key : countersList.keySet()) {
	    TimerSnapshot timerSnapshot = countersList.get(key);
	    Timer timer = registry.timer(key);
	    timerSnapshot.setValues(timer.getSnapshot().getValues());
	}

	return regSnapshot;

    }

    public static CounterSnapshot mergeCounters(String metricName) {
	registriesMap = getRegistriesMap();
	CounterSnapshot globalCounter = new CounterSnapshot();
	long total = 0;
	for (Object key : registriesMap.keySet()) {
	    MetricRegistrySnapshot rs = registriesMap.get(key);
	    CounterSnapshot counter1Snapshot = rs.counter(metricName);
	    total += counter1Snapshot.getCount();
	}
	globalCounter.setCount(total);
	return globalCounter;

    }

    public static MeterSnapshot mergeMeters(String metricName, MergeFormula minutesRateFormula, MergeFormula meanRateFormula) {

	MeterSnapshot globalMeter = new MeterSnapshot();
	long count = 0;
	registriesMap = getRegistriesMap();
	List<MeterSnapshot> meters = new ArrayList<MeterSnapshot>();
	for (Object key : registriesMap.keySet()) {
	    MetricRegistrySnapshot rs = registriesMap.get(key);
	    MeterSnapshot meter = rs.meter(metricName);
	    count += meter.getCount();
	    meters.add(meter);
	}
	minutesRateFormula.mergeMinutesRate(meters, globalMeter);
	meanRateFormula.mergeMeanRate(meters, globalMeter);
	globalMeter.setCount(count);
	return globalMeter;
    }

    public static TimerSnapshot mergeTimers(String metricName, MergeFormula minutesRateFormula, MergeFormula meanRateFormula, MergeFormula stddevFormula, MergeFormula meanFormula, MergeFormula percentageFormula) {
	TimerSnapshot globalTimer = null;
	long count = 0;
	globalTimer = new TimerSnapshot();
	registriesMap = getRegistriesMap();
	TimerSnapshot timer = null;
	List <TimerSnapshot> timers = new ArrayList<TimerSnapshot>();
	for (Object key : registriesMap.keySet()) {
	    MetricRegistrySnapshot rs = registriesMap.get(key);
	    timer = rs.timer(metricName);
	    timers.add(timer);
	    count += timer.getCount();   
	}

	if (timers.size() > 0) {

	    globalTimer.setCount(count);
	    globalTimer.setDuration_units(timer.getDurationUnits());
	    globalTimer.setRate_units(timer.getRateUnits());
	    minutesRateFormula.mergeMinutesRate(timers, globalTimer);
	    meanRateFormula.mergeMeanRate(timers, globalTimer);
	    stddevFormula.mergeStdDev(timers, globalTimer);
	    meanFormula.mergeMean(timers, globalTimer);
	    percentageFormula.mergePercentages(timers, globalTimer);
	}
	return globalTimer;

    }

    public CounterSnapshot findCounter(String exp) {
	return find(counters, exp);
    }

    public Map<String, CounterSnapshot> findCounters(String exp) {
	return findAll(counters, exp);
    }

    public TimerSnapshot findTimer(String exp) {
	return find(timers, exp);
    }

    public Map<String, TimerSnapshot> findTimers(String exp) {
	return findAll(timers, exp);
    }
    
    public void filter(String exp) {
        if(exp == null) return ;
        filter(counters, exp) ;
        filter(timers, exp) ;
      }
    
    private <T> T find(Map<String, T> map, String exp) {
	exp.replace("*", ".*");
	Pattern pattern = Pattern.compile(exp);
	for (Map.Entry<String, T> entry : map.entrySet()) {
	    String key = entry.getKey();
	    if (pattern.matcher(key).matches())
		return entry.getValue();
	}
	return null;
    }

    private <T> Map<String, T> findAll(Map<String, T> map, String exp) {
	exp = exp.replace("*", ".*");
	Map<String, T> holder = new HashMap<String, T>();
	Pattern pattern = Pattern.compile(exp);
	for (Map.Entry<String, T> entry : map.entrySet()) {
	    String key = entry.getKey();
	    if (pattern.matcher(key).matches())
		holder.put(key, entry.getValue());
	}
	return holder;
    }
    
    private <T> void filter(Map<String, T> map, String exp) {
        exp = exp.replace("*", ".*") ;
        Pattern pattern = Pattern.compile(exp) ;
        Iterator<Map.Entry<String, T>> i = map.entrySet().iterator() ;
        while(i.hasNext()) {
          Map.Entry<String, T> entry = i.next() ;
          String key = entry.getKey() ;
          if(!pattern.matcher(key).matches()) i.remove();
        }
    }
    
    public static void updateRemoteMap(String producerName,
    	    MetricRegistrySnapshot regSnapshot) {
    	registriesMap = getRegistriesMap();
    	registriesMap.put(producerName, regSnapshot);
        }
}
