package com.neverwinterdp.util.monitor.snapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.neverwinterdp.util.monitor.mergestrategy.MergeFormula;

public class MeterSnapshot implements Serializable {
    long count;
    double m15_rate;
    double m1_rate;
    double m5_rate;
    double mean_rate;
    String units;

    public MeterSnapshot(){
	
    }
    public MeterSnapshot(MeterSnapshot clone){
    	this.count = clone.count;
    	this.m15_rate = clone.m15_rate;
    	this.m1_rate = clone.m1_rate;
    	this.m5_rate = clone.m5_rate;
    	this.mean_rate = clone.mean_rate;
    	this.units = clone.units;
    	
    }

    public long getCount() {
	return count;
    }

    public void setCount(long count) {
	this.count = count;
    }

    public double getM15Rate() {
	return m15_rate;
    }

    public void setM15_rate(double m15_rate) {
	this.m15_rate = m15_rate;
    }

    public double getM1Rate() {
	return m1_rate;
    }

    public void setM1_rate(double m1_rate) {
	this.m1_rate = m1_rate;
    }

    public double getM5Rate() {
	return m5_rate;
    }

    public void setM5_rate(double m5_rate) {
	this.m5_rate = m5_rate;
    }

    public double getMeanRate() {
	return mean_rate;
    }

    public void setMean_rate(double mean_rate) {
	this.mean_rate = mean_rate;
    }

    public String getUnits() {
	return units;
    }

    public void setUnits(String units) {
	this.units = units;
    }
   
    public void merge(MeterSnapshot other,  MergeFormula minutesRateFormula, MergeFormula meanRateFormula) {
    	List<MeterSnapshot> meters = new ArrayList<MeterSnapshot>();
    	meters.add(this);
    	meters.add(other);
    	minutesRateFormula.mergeMinutesRate(meters, this);
    	meanRateFormula.mergeMeanRate(meters, this);
    	setCount(count + other.getCount());
    }
 
}
