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

public class MeterMergeUnitTest {
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
	MeterSnapshot globalMeter = MetricRegistrySnapshot.mergeMeters(
		"meterSample", af, af);
	assertEquals(m1Snapshot.getCount() + m2Snapshot.getCount(),globalMeter.getCount());
	assertEquals("" , (m1Snapshot.getMeanRate() + m2Snapshot.getMeanRate())/ 2, globalMeter.getMeanRate(), delta);
	assertEquals("" , (m1Snapshot.getM15Rate() + m2Snapshot.getM15Rate())/ 2, globalMeter.getM15Rate() , delta);
	assertEquals("" , (m1Snapshot.getM5Rate() + m2Snapshot.getM5Rate())/ 2, globalMeter.getM5Rate() , delta);
	assertEquals("" , (m1Snapshot.getM1Rate() + m2Snapshot.getM1Rate())/ 2, globalMeter.getM1Rate(), delta);

	// merge the meters with Average formula
	
	List<Double> weightList = new ArrayList<Double>();
	for (String key : MetricRegistrySnapshot.getRegistriesMap().keySet()) {
	    MetricRegistrySnapshot rs = MetricRegistrySnapshot.getRegistriesMap().get(key);
	    weightList.add(rs.meter("meterSample").getMeanRate());
	
	}
	double[] weights = new double[weightList.size()];
	for(int i=0; i < weightList.size();i++){
	    weights[i] = weightList.get(i);
	}
	MergeFormula waf = new WeightedAverageFormula(weights);
	
	MeterSnapshot globalSnapshotWeightedAverage = MetricRegistrySnapshot.mergeMeters("meterSample", waf, af);
	
	assertEquals(m1Snapshot.getCount() + m2Snapshot.getCount(),globalMeter.getCount());
	assertEquals("" , (m1Snapshot.getMeanRate() + m2Snapshot.getMeanRate())/ 2, globalMeter.getMeanRate(), delta);
	assertEquals("" , (m1Snapshot.getM15Rate() * weights[1] + m2Snapshot.getM15Rate() * weights[0] ) / (weights[0]+ weights[1]), globalMeter.getM15Rate(), delta);
	DecimalFormat df = new DecimalFormat("0.000000"); 
	System.out.println("Count node 1 : "+df.format(m1Snapshot.getCount())+" Count node 2 : "+df.format(m2Snapshot.getCount())+" \nSimple Average  Count : "+df.format(globalMeter.getCount())+"   Weigted Average Count : "+df.format(globalSnapshotWeightedAverage.getCount()));
	System.out.println("");
	System.out.println("Mean Rate node 1 : "+df.format(m1Snapshot.getMeanRate())+" Mean rate node 2 : "+df.format(m2Snapshot.getMeanRate())+" \nSimple Average Mean Rate : "+df.format(globalMeter.getMeanRate())+"   Weigted Average Mean Rate : "+df.format(globalSnapshotWeightedAverage.getMeanRate()));
	System.out.println("");
	System.out.println("M15 node 1 : "+df.format(m1Snapshot.getM15Rate()) +" M15 node 2 : "+df.format(m2Snapshot.getM15Rate())+" \nSimple Average M15 : "+df.format(globalMeter.getM15Rate())+"   Weigted Average M15 : "+df.format(globalSnapshotWeightedAverage.getM15Rate()));

    }
}
