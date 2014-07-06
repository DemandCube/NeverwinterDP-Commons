package com.neverwinterdp.util.monitor;

import static org.junit.Assert.assertEquals;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.neverwinterdp.util.monitor.mergestrategy.AverageFormula;
import com.neverwinterdp.util.monitor.mergestrategy.MergeFormula;
import com.neverwinterdp.util.monitor.mergestrategy.WeightedAverageFormula;
import com.neverwinterdp.util.monitor.snapshot.MetricRegistrySnapshot;
import com.neverwinterdp.util.monitor.snapshot.TimerSnapshot;

public class TimerMergeTest {

    @Test
    public void test() throws InterruptedException {
	// Node 1
	MetricRegistry registry1 = new MetricRegistry();
	Timer timer1 = registry1.timer("timerSample");
	Timer.Context ctx = timer1.time();
	ctx.stop();
	for(int i= 0; i < 5; i++){
	    ctx = timer1.time();
	    ctx.stop();
	    Thread.sleep(1000);
	}
	MetricRegistrySnapshot regSnapshot1 = MetricRegistrySnapshot.convert(registry1);
	MetricRegistrySnapshot.updateRemoteMap("producer1",regSnapshot1);
	TimerSnapshot timer1Snapshot = regSnapshot1.timer("timerSample");
	
	// Node 2
	MetricRegistry registry2 = new MetricRegistry();
	Timer timer2 = registry2.timer("timerSample");
	Timer.Context ctx2 = timer2.time();
	ctx2.stop();
	for(int i= 0; i < 10; i++){
	    ctx2 = timer2.time();
	    ctx2.stop();
	    Thread.sleep(500);
	}
	MetricRegistrySnapshot regSnapshot2 = MetricRegistrySnapshot.convert(registry2);
	MetricRegistrySnapshot.updateRemoteMap("producer2",regSnapshot2);
	TimerSnapshot timer2Snapshot = regSnapshot2.timer("timerSample");
	
	// Simple average formula
	MergeFormula af = new AverageFormula();
	TimerSnapshot globalSnapshot = MetricRegistrySnapshot.mergeTimers("timerSample",af,af,af,af,af);
	assertEquals(timer1Snapshot.getCount() + timer2Snapshot.getCount(), globalSnapshot.getCount());
	assertEquals(""+(timer1Snapshot.getMeanRate() + timer2Snapshot.getMeanRate())/2,globalSnapshot.getMeanRate()+"");

	
	// weighted average formula

	
	List<Double> weightList = new ArrayList<Double>();
	for (String key : MetricRegistrySnapshot.getRegistriesMap().keySet()) {
	    MetricRegistrySnapshot rs = MetricRegistrySnapshot.getRegistriesMap().get(key);
	    weightList.add(rs.timer("timerSample").getMeanRate());
	
	}
	double[] weights = new double[weightList.size()];
	for(int i=0; i < weightList.size();i++){
	    weights[i] = weightList.get(i);
	}

	MergeFormula waf = new WeightedAverageFormula(weights) ;
	TimerSnapshot globalSnapshotWeightedAverage = MetricRegistrySnapshot.mergeTimers("timerSample",waf, af,waf,waf,waf);
	double delta = 0.001d;
	assertEquals(timer1Snapshot.getCount() + timer2Snapshot.getCount(), globalSnapshot.getCount());
	assertEquals("",(timer1Snapshot.getMeanRate() + timer2Snapshot.getMeanRate())/2,globalSnapshot.getMeanRate(),delta);
	assertEquals("" ,(timer1Snapshot.getM15Rate() * weights[0] + timer1Snapshot.getM15Rate() * weights[1] ) / (weights[0]+ weights[1]), timer1Snapshot.getM15Rate(),delta );
	assertEquals("" , (timer1Snapshot.getP50() * weights[0] + timer1Snapshot.getP50() * weights[1] ) / (weights[0]+ weights[1]), timer1Snapshot.getP50() ,delta);
	assertEquals("",(timer1Snapshot.getStddev() * weights[0] + timer1Snapshot.getStddev() * weights[1] ) / (weights[0]+ weights[1]), timer1Snapshot.getStddev(),delta );
	DecimalFormat df = new DecimalFormat("0.000000"); 
	System.out.println("Mean Rate node 1 : "+df.format(timer1Snapshot.getMeanRate())+" Mean rate node 2 : "+df.format(timer2Snapshot.getMeanRate()));
	System.out.println("");
	System.out.println("M15 node 1 : "+df.format(timer1Snapshot.getM15Rate()) +" M15 node 2 : "+df.format(timer2Snapshot.getM15Rate())+" \nSimple Average M15 : "+df.format(globalSnapshot.getM15Rate())+"   Weigted Average M15 : "+df.format(globalSnapshotWeightedAverage.getM15Rate()));
	System.out.println("");
	System.out.println("P50 node 1 : "+df.format(timer1Snapshot.getP50())+" P50 node 2 : "+df.format(timer2Snapshot.getP50())+" \nSimple Average P50 : "+df.format(globalSnapshot.getP50())+"   Weigted Average P50 : "+df.format(globalSnapshotWeightedAverage.getP50()));
	System.out.println("");
	System.out.println("Mean node 1 : "+df.format(timer1Snapshot.getMean())+" Mean node 2 : "+df.format(timer2Snapshot.getMean())+" \nSimple Average Mean : "+df.format(globalSnapshot.getMean())+"   Weigted Average Mean : "+df.format(globalSnapshotWeightedAverage.getMean()));
	System.out.println("");
	System.out.println("Stddev node 1 : "+df.format(timer1Snapshot.getStddev()) +" Stddev node 2 : "+df.format(timer2Snapshot.getStddev())+" \nSimple Average Stddev : "+df.format(globalSnapshot.getStddev())+"   Weigted Average Stddev : "+df.format(globalSnapshotWeightedAverage.getStddev()));
	
	
    }

}
