package com.neverwinterdp.util.monitor.snapshot;

import java.io.Serializable;

import com.codahale.metrics.Counter;

/**
 * @author Tuan Nguyen
 * @email tuan08@gmail.com
 */
public class CounterSnapshot implements Serializable {
    private long count;

    public CounterSnapshot() {
    }

    CounterSnapshot(Counter counter) {
	this.count = counter.getCount();
    }

    public long getCount() {
	return count;
    }

    public void setCount(long count) {
	this.count = count;
    }

    // TODO: to implement
    public void merge(CounterSnapshot other) {

    }
}
