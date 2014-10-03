package com.neverwinterdp.yara.server;

import org.junit.Test ;

import com.neverwinterdp.yara.LogRequestSerializer;
import com.neverwinterdp.yara.protocol.CounterLog;
import com.neverwinterdp.yara.protocol.LogRequest;
import com.neverwinterdp.yara.protocol.TimerLog;

public class ProtocolUnitTest {
  @Test
  public void testSerialization() {
    LogRequest logRequest = createLogRequest() ;
    LogRequestSerializer serializer = new LogRequestSerializer() ;
    byte[] data = serializer.toBytes(logRequest) ;
    LogRequest logRequestClone = serializer.fromBytes(data) ;
  }
  
  LogRequest createLogRequest() {
    LogRequest.Builder reqB = LogRequest.newBuilder() ;
    reqB.setServer("localhost") ;
    for(int i = 0; i < 10; i++) {
      CounterLog.Builder counterB = CounterLog.newBuilder() ;
      counterB.setName("counter." + i) ;
      counterB.setCount(1) ;
      counterB.setTimestampTick(100) ;
      reqB.addCounter(counterB.build()) ;
      
      TimerLog.Builder timerB = TimerLog.newBuilder() ;
      timerB.setName("timer." + i) ;
      timerB.setTimestampTick(System.currentTimeMillis()) ;
      timerB.setDuration(i * 1000) ;
      reqB.addTimer(timerB.build()) ;
    }
    return reqB.build() ;
  }
}
