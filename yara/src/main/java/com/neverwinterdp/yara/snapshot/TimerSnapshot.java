package com.neverwinterdp.yara.snapshot;

import com.neverwinterdp.yara.Histogram;
import com.neverwinterdp.yara.Timer;

public class TimerSnapshot {
  private long   count;
  private long   min;
  private long   max;
  private double mean;
  private double stddev;
  private double p75;
  private double p90;
  private double p95;
  private double p99;
  private double p99_999;
  private double m1Rate;
  private double m5Rate;
  private double m15Rate;
  private double meanRate;

  public TimerSnapshot() {
  }

  public TimerSnapshot(Timer timer) {
    Histogram histogram = timer.getHistogram();
    count = timer.getCount();
    min = histogram.getMin();
    max = histogram.getMax();
    mean = histogram.getMean();
    stddev = histogram.getStdDev();
    p75 = histogram.getQuantile(0.75);
    p90 = histogram.getQuantile(0.90);
    p95 = histogram.getQuantile(0.95);
    p99 = histogram.getQuantile(0.99);
    p99_999 = histogram.getQuantile(0.99999);
    m1Rate = timer.getOneMinuteRate();
    m5Rate = timer.getFiveMinuteRate();
    m15Rate = timer.getFifteenMinuteRate();
    meanRate = timer.getMeanRate();
  }

  public long getCount() { return count; }

  public long getMin() { return min; }

  public long getMax() { return max; }

  public double getMean() { return mean; }

  public double getStddev() { return stddev; }

  public double getP75() { return p75; }

  public double getP90() { return p90; }

  public double getP95() { return p95; }

  public double getP99() { return p99; }

  public double getP99_999() { return p99_999; }

  public double getM1Rate() { return m1Rate; }

  public double getM5Rate() { return m5Rate; }

  public double getM15Rate() { return m15Rate; }

  public double getMeanRate() { return meanRate; }
}