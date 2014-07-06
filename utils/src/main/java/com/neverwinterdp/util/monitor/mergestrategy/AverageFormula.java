package com.neverwinterdp.util.monitor.mergestrategy;

import java.util.Arrays;
import java.util.List;
import com.codahale.metrics.Snapshot;
import com.neverwinterdp.util.monitor.snapshot.MeterSnapshot;
import com.neverwinterdp.util.monitor.snapshot.TimerSnapshot;
public class AverageFormula implements MergeFormula {

    long[] values = new long[0];
    @Override
    public void mergePercentages(List<TimerSnapshot> timers,
	    TimerSnapshot mergedtimer) {
	
	for (TimerSnapshot timer : timers) {
	    values = concat(values, timer.getValues());
	}
	Arrays.sort(values);
	Snapshot snapshot = new Snapshot(values);
	mergedtimer.setP50(snapshot.getValue(0.5)/1000000);
	mergedtimer.setP75(snapshot.getValue(0.75)/1000000);
	mergedtimer.setP95(snapshot.getValue(0.95)/1000000);
	mergedtimer.setP98(snapshot.getValue(0.98)/1000000);
	mergedtimer.setP99(snapshot.getValue(0.99)/1000000);
	mergedtimer.setP999(snapshot.getValue(0.999)/1000000);
	mergedtimer.setMin(snapshot.getMin());
	mergedtimer.setMax(snapshot.getMax());
	mergedtimer.setMin(snapshot.getMin());
	

    }
    private long[] concat(long[] A, long[] B) {
	   int aLen = A.length;
	   int bLen = B.length;
	   long[] C= new long[aLen+bLen];
	   System.arraycopy(A, 0, C, 0, aLen);
	   System.arraycopy(B, 0, C, aLen, bLen);
	   return C;
	}


    @Override
    public void mergeStdDev(List<TimerSnapshot> timers,
	    TimerSnapshot mergedtimer) {
	for (TimerSnapshot timer : timers) {
	    values = concat(values, timer.getValues());
	}
	Arrays.sort(values);
	Snapshot snapshot = new Snapshot(values);
	mergedtimer.setStddev(snapshot.getStdDev()/1000000);
    }

    @Override
    public void mergeMean(List<TimerSnapshot> timers, TimerSnapshot mergedtimer) {
	double mean = 0;
	for (TimerSnapshot timer : timers) {
	    mean += timer.getMean();
	}
	mergedtimer.setMean(mean / timers.size());

    }

    @Override
    public void mergeMinutesRate(List<MeterSnapshot> meters,
	    MeterSnapshot mergedmeter) {
	double m15_rate = 0;
	double m1_rate = 0;
	double m5_rate = 0;
	int size = meters.size();
	for (MeterSnapshot meter : meters) {
	    m15_rate += meter.getM15Rate();
	    m1_rate += meter.getM1Rate();
	    m5_rate += meter.getM5Rate();
	}
	mergedmeter.setM1Rate(m1_rate / size);
	mergedmeter.setM5Rate(m5_rate / size);
	mergedmeter.setM15Rate(m15_rate / size);

    }

    @Override
    public void mergeMeanRate(List<MeterSnapshot> meters,
	    MeterSnapshot mergedmeter) {
	double mean_rate = 0;
	for (MeterSnapshot meter : meters) {
	    mean_rate += meter.getMeanRate();
	}
	mergedmeter.setMeanRate(mean_rate / meters.size());

    }

    @Override
    public void mergeMinutesRate(List<TimerSnapshot> timers,
	    TimerSnapshot Mergedtimer) {
	double m15_rate = 0;
	double m1_rate = 0;
	double m5_rate = 0;
	int size = timers.size();
	for (TimerSnapshot timer : timers) {
	    m15_rate += timer.getM15Rate();
	    m1_rate += timer.getM15Rate();
	    m5_rate += timer.getM15Rate();

	}
	Mergedtimer.setM1_rate(m1_rate / size);
	Mergedtimer.setM5_rate(m5_rate / size);
	Mergedtimer.setM15_rate(m15_rate / size);

    }

    @Override
    public void mergeMeanRate(List<TimerSnapshot> timers,
	    TimerSnapshot mergedtimer) {
	double mean_rate = 0;
	for (TimerSnapshot timer : timers) {
	    mean_rate += timer.getMeanRate();
	}
	mergedtimer.setMean_rate(mean_rate / timers.size());

    }

}
