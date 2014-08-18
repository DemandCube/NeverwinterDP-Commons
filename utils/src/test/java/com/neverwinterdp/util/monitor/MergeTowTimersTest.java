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

public class MergeTowTimersTest {

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
	TimerSnapshot oldTimer1Snapshot = new TimerSnapshot(timer1Snapshot);
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
	double delta = 0.01d;
	// Simple average formula
	MergeFormula af = new AverageFormula();
	timer1Snapshot.merge(timer2Snapshot,af,af,af,af,af);
	assertEquals(oldTimer1Snapshot.getCount() + timer2Snapshot.getCount(), timer1Snapshot.getCount());
	assertEquals("",(oldTimer1Snapshot.getMeanRate() + timer2Snapshot.getMeanRate()) / 2,timer1Snapshot.getMeanRate(),delta);
	assertEquals("" ,(oldTimer1Snapshot.getM15Rate() + timer2Snapshot.getM15Rate()) / 2, timer1Snapshot.getM15Rate(),delta );
	assertEquals("" , (oldTimer1Snapshot.getP50() + timer2Snapshot.getP50() / 2 ), timer1Snapshot.getP50() ,delta);
	assertEquals("",(oldTimer1Snapshot.getStddev() + timer2Snapshot.getStddev() / 2 ), timer1Snapshot.getStddev(),delta );

    }

}
