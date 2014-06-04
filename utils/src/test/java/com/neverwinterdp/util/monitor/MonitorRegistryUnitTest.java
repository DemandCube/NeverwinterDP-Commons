package com.neverwinterdp.util.monitor;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.neverwinterdp.util.monitor.snapshot.CounterSnapshot;
import com.neverwinterdp.util.monitor.snapshot.MonitorRegistrySnapshot;
import com.neverwinterdp.util.monitor.snapshot.TimerSnapshot;

public class MonitorRegistryUnitTest {  
  @Test
  public void testSerialization() throws Exception {
    ObjectMapper mapper = new ObjectMapper() ; 
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.MILLISECONDS, false));
    
    MonitorRegistry registry = new MonitorRegistry("localhost", "test") ;
    Timer timer1 = registry.timer("timer1") ;
    Timer.Context ctx = timer1.time();
    Counter counter1 = registry.counter("counter1") ;
    counter1.inc(); 
    Counter counter2 = registry.counter("counter2") ;
    counter2.inc();
    ctx.stop() ;
    String json = mapper.writeValueAsString(registry) ;
    System.out.println(json) ;
    
    StringReader reader = new StringReader(json) ;
    MonitorRegistrySnapshot regSnapshot = mapper.readValue(reader, MonitorRegistrySnapshot.class) ;
    
    CounterSnapshot counter1Snapshot = regSnapshot.counter("counter1") ;
    assertEquals(counter1.getCount(), counter1Snapshot.getCount()) ;
    
    CounterSnapshot counter2Snapshot = regSnapshot.counter("counter2") ;
    assertEquals(counter2.getCount(), counter2Snapshot.getCount()) ;
    
    TimerSnapshot timer1Snapshot = regSnapshot.timer("timer1") ;
    assertEquals(timer1.getCount(), timer1Snapshot.getCount()) ;
  }
}
