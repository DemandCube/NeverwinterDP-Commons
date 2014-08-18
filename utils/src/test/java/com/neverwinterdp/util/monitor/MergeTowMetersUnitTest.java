package com.neverwinterdp.util.monitor;

import static org.junit.Assert.assertEquals;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.neverwinterdp.util.monitor.mergestrategy.AverageFormula;
import com.neverwinterdp.util.monitor.mergestrategy.MergeFormula;
import com.neverwinterdp.util.monitor.mergestrategy.WeightedAverageFormula;
import com.neverwinterdp.util.monitor.snapshot.MeterSnapshot;
import com.neverwinterdp.util.monitor.snapshot.MetricRegistrySnapshot;

public class MergeTowMetersUnitTest {
    @Test
    public void testSerialization() throws Exception {
	// Node 1
	MetricRegistry registry1 = new MetricRegistry();
	Meter m1 = registry1.meter("meterSample");
	m1.mark(5);
	Thread.sleep(5000);
	m1.mark(3);

	MetricRegistrySnapshot regSnapshot1 = MetricRegistrySnapshot
		.convert(registry1);
	MeterSnapshot m1Snapshot = regSnapshot1.meter("meterSample");
	MetricRegistrySnapshot.updateRemoteMap("producer1", regSnapshot1);

	// Node 2
	MetricRegistry registry2 = new MetricRegistry();
	Meter m2 = registry2.meter("meterSample");
	m2.mark(3);
	Thread.sleep(5000);
	m2.mark(4);
	MetricRegistrySnapshot regSnapshot2 = MetricRegistrySnapshot
		.convert(registry2);
	MeterSnapshot m2Snapshot = regSnapshot2.meter("meterSample");
	MetricRegistrySnapshot.updateRemoteMap("producer2", regSnapshot2);

	// merge the meters with Average formula
	MergeFormula af = new AverageFormula();
	double delta = 0.01d;
	MeterSnapshot oldM1Snapshot = new MeterSnapshot(m1Snapshot);
	m1Snapshot.merge(m2Snapshot, af, af);

	assertEquals(oldM1Snapshot.getCount() + m2Snapshot.getCount(),m1Snapshot.getCount());
	assertEquals("" , (oldM1Snapshot.getMeanRate() + m2Snapshot.getMeanRate())/ 2, m1Snapshot.getMeanRate(), delta);
	assertEquals("" , (oldM1Snapshot.getM15Rate() + m2Snapshot.getM15Rate())/ 2, m1Snapshot.getM15Rate() , delta);
	assertEquals("" , (oldM1Snapshot.getM5Rate() + m2Snapshot.getM5Rate())/ 2, m1Snapshot.getM5Rate() , delta);
	assertEquals("" , (oldM1Snapshot.getM1Rate() + m2Snapshot.getM1Rate())/ 2, m1Snapshot.getM1Rate(), delta);

	
    }
}
