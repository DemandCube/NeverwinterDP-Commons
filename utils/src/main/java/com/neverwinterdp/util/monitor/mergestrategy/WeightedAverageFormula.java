package com.neverwinterdp.util.monitor.mergestrategy;

import java.util.List;
import com.neverwinterdp.util.monitor.snapshot.MeterSnapshot;
import com.neverwinterdp.util.monitor.snapshot.TimerSnapshot;
public class WeightedAverageFormula implements MergeFormula {

    private double[] weights;
    private double totalWeights;
    long[] values = new long[0];
    
    public WeightedAverageFormula(double[] weights){
	this.weights = weights;
	for(int i=0; i < weights.length; i++){
	    totalWeights+= weights[i];
	}
    }
    @Override
    public void mergePercentages(List<TimerSnapshot> timers,
	    TimerSnapshot mergedtimer) {
	int i=0;
	double p50 = 0,p75 = 0, p95 = 0, p98 = 0, p99 = 0, p999 = 0;
	for (TimerSnapshot timer : timers) {
	    p50 += timer.getP50() * weights[i];
	    p75 += timer.getP75() * weights[i];
	    p95 += timer.getP95 () * weights[i];
	    p98 += timer.getP98 () * weights[i];
	    p99 += timer.getP99 () * weights[i];
	    p999 += timer.getP999 () * weights[i];
	    i++;
	}
	mergedtimer.setP50(p50/totalWeights);
	mergedtimer.setP75(p75/totalWeights);
	mergedtimer.setP95(p95/totalWeights);
	mergedtimer.setP98(p98/totalWeights);
	mergedtimer.setP99(p99/totalWeights);
	mergedtimer.setP999(p999/totalWeights);

    }

    @Override
    public void mergeStdDev(List<TimerSnapshot> timers,
	    TimerSnapshot mergedtimer) {
	double stddev =0; int i=0;
	for (TimerSnapshot timer : timers)
	{
	    stddev+= timer.getStddev() * weights[i];
	    i++;
	}
	mergedtimer.setStddev(stddev / totalWeights);
    }

    @Override
    public void mergeMean(List<TimerSnapshot> timers, TimerSnapshot mergedtimer) {
	double mean = 0; int i =0;
	for (TimerSnapshot timer : timers) {
	    mean += timer.getMean() * weights[i];
	    i++;
	}
	mergedtimer.setMean(mean / totalWeights);

    }

    @Override
    public void mergeMinutesRate(List<MeterSnapshot> meters,
	    MeterSnapshot mergedmeter) {
	double m15_rate = 0;
	double m1_rate = 0;
	double m5_rate = 0;
	int i=0;
	for (MeterSnapshot meter : meters) {
	    m15_rate += meter.getM15Rate() * weights[i];
	    m1_rate += meter.getM1Rate() * weights[i];
	    m5_rate += meter.getM5Rate() * weights[i];
	    i++;
	}
	mergedmeter.setM1Rate(m1_rate / totalWeights);
	mergedmeter.setM5Rate(m5_rate / totalWeights);
	mergedmeter.setM15Rate(m15_rate / totalWeights);

    }

    @Override
    public void mergeMeanRate(List<MeterSnapshot> meters,
	    MeterSnapshot mergedmeter) {
	double mean_rate = 0;int i = 0;
	for (MeterSnapshot meter : meters) {
	    mean_rate += meter.getMeanRate() * weights[i];
	    i++;
	}
	mergedmeter.setMeanRate(mean_rate / totalWeights);

    }

    @Override
    public void mergeMinutesRate(List<TimerSnapshot> timers,
	    TimerSnapshot Mergedtimer) {
	double m15_rate = 0;
	double m1_rate = 0;
	double m5_rate = 0;
	int i =0;
	for (TimerSnapshot timer : timers) {
	    m15_rate += timer.getM15Rate() * weights[i];
	    m1_rate += timer.getM15Rate() * weights[i];
	    m5_rate += timer.getM15Rate() * weights[i];
	    i++;

	}
	Mergedtimer.setM1_rate(m1_rate / totalWeights);
	Mergedtimer.setM5_rate(m5_rate / totalWeights);
	Mergedtimer.setM15_rate(m15_rate / totalWeights);

    }

    @Override
    public void mergeMeanRate(List<TimerSnapshot> timers,
	    TimerSnapshot mergedtimer) {
	double mean_rate = 0;int i = 0;
	for (TimerSnapshot timer : timers) {
	    mean_rate += timer.getMeanRate() * weights[i];
	    i++;
	}
	mergedtimer.setMean_rate(mean_rate / totalWeights);

    }

}
