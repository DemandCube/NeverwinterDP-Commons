package com.neverwinterdp.util.monitor.mergestrategy;

import java.util.List;
import com.neverwinterdp.util.monitor.snapshot.MeterSnapshot;
import com.neverwinterdp.util.monitor.snapshot.TimerSnapshot;

public interface MergeFormula {
    public void mergePercentages(List<TimerSnapshot> timers, TimerSnapshot Mergedtimer);
    public void mergeStdDev(List<TimerSnapshot> timers, TimerSnapshot Mergedtimer);
    public void mergeMean(List<TimerSnapshot> timers, TimerSnapshot Mergedtimer);
    public void mergeMinutesRate(List<MeterSnapshot>  meters, MeterSnapshot Mergedmeter);
    public void mergeMeanRate(List<MeterSnapshot>  meters, MeterSnapshot Mergedmeter);
    public void mergeMinutesRate(List<TimerSnapshot>  timers, TimerSnapshot Mergedtimer);
    public void mergeMeanRate(List<TimerSnapshot>  timers, TimerSnapshot Mergedtimer);

}
