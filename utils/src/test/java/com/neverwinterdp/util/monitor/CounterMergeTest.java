package com.neverwinterdp.util.monitor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.neverwinterdp.util.monitor.snapshot.CounterSnapshot;
import com.neverwinterdp.util.monitor.snapshot.MetricRegistrySnapshot;

public class CounterMergeTest {

    @Test
    public void test() {
	// Node 1 
	MetricRegistry registry1 = new MetricRegistry();
	Counter counter1 = registry1.counter("counterSample");
	counter1.inc();
	MetricRegistrySnapshot regSnapshot1 = MetricRegistrySnapshot.convert(registry1);
	MetricRegistrySnapshot.updateRemoteMap("producer1",regSnapshot1);
	
	// Node 2
	MetricRegistry registry2 = new MetricRegistry();
	Counter counter2 = registry2.counter("counterSample");
	counter2.inc();
	MetricRegistrySnapshot regSnapshot2 = MetricRegistrySnapshot.convert(registry2);
	MetricRegistrySnapshot.updateRemoteMap("producer2",regSnapshot2);
	
	// Node 3, Merging the metrics
	CounterSnapshot globalCounter = MetricRegistrySnapshot.mergeCounters("counterSample");
	assertEquals(2, globalCounter.getCount());

    }

}
