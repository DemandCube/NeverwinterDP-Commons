package com.neverwinterdp.util.monitor;

import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class MonitorRegistryUnitTest {  
  @Test
  public void testSerialization() throws Exception {
    ObjectMapper mapper = new ObjectMapper() ; 
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.MILLISECONDS, false));
    
    MonitorRegistry registry = new MonitorRegistry("localhost", "test") ;
    Timer.Context ctx = registry.timer("timer1").time();
    Counter counter1 = registry.counter("counter1") ;
    counter1.inc(); 
    Counter counter2 = registry.counter("counter2") ;
    counter2.inc();
    ctx.stop() ;
    String json = mapper.writeValueAsString(registry) ;
    System.out.println(json) ;
    StringReader reader = new StringReader(json) ;
    registry = mapper.readValue(reader, MonitorRegistry.class) ;
  }
}
