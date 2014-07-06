package com.neverwinterdp.util.monitor.snapshot;

import java.io.Serializable;

public class MeterSnapshot implements Serializable {
    long count;
    double m15_rate;
    double m1_rate;
    double m5_rate;
    double mean_rate;

    public MeterSnapshot(){
	
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

    public void setM15Rate(double m15_rate) {
	this.m15_rate = m15_rate;
    }

    public double getM1Rate() {
	return m1_rate;
    }

    public void setM1Rate(double m1_rate) {
	this.m1_rate = m1_rate;
    }

    public double getM5Rate() {
	return m5_rate;
    }

    public void setM5Rate(double m5_rate) {
	this.m5_rate = m5_rate;
    }

    public double getMeanRate() {
	return mean_rate;
    }

    public void setMeanRate(double mean_rate) {
	this.mean_rate = mean_rate;
    }

    public String getUnits() {
	return units;
    }

    public void setUnits(String units) {
	this.units = units;
    }

    String units;

}
