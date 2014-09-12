package com.neverwinterdp.yara;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

public class Counter implements Serializable {
  private AtomicLong counter = new AtomicLong() ;

  public long getCount() { return counter.longValue() ; }
  
  public long incr() {
    return counter.addAndGet(1l);
  }
  
  public long incr(long n) {
    return counter.addAndGet(n);
  }
  
  public long decr() {
    return counter.decrementAndGet();
  }
  
  static public Counter combine(Counter ... counters) {
    Counter counter = new Counter() ;
    for(Counter sel : counters) {
      counter.incr(sel.getCount()) ;
    }
    return counter ;
  }
  
  static public Counter combine(Collection<Counter>  counters) {
    Counter[] array = new Counter[counters.size()] ;
    counters.toArray(array) ;
    return combine(array) ;
  }
}
