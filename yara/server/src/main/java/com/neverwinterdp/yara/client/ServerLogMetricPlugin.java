package com.neverwinterdp.yara.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Clock;
import com.neverwinterdp.buffer.chronicle.MultiSegmentQueue;
import com.neverwinterdp.buffer.chronicle.Segment;
import com.neverwinterdp.yara.LogRequestSerializer;
import com.neverwinterdp.yara.MetricPlugin;
import com.neverwinterdp.yara.protocol.CounterLog;
import com.neverwinterdp.yara.protocol.LogRequest;
import com.neverwinterdp.yara.protocol.TimerLog;

public class ServerLogMetricPlugin implements MetricPlugin {
  final static long SECOND_IN_NANO = TimeUnit.SECONDS.toNanos(1) ;
  
  private String                    serverName;
  private MultiSegmentQueue<LogRequest>     queue ;
  private LogRequestForwarder       forwarder;
  private BlockingQueue<CounterLog> counters = new LinkedBlockingQueue<CounterLog>();
  private BlockingQueue<TimerLog>   timers   = new LinkedBlockingQueue<TimerLog>();
  private int flushSize = 1000 ;
  private long lastFlushTime = 0;
  private ServerLogDeamon deamon ;
  
  public ServerLogMetricPlugin(String serverName, String storeDir) throws Exception {
    this.serverName = serverName; 
    queue = new MultiSegmentQueue<LogRequest>(storeDir, new LogRequestSerializer(), 50000) ;
    deamon = new ServerLogDeamon() ;
    deamon.start();
  }
  
  public ServerLogMetricPlugin(String serverName, String storeDir, LogRequestForwarder forwarder) throws Exception {
    this(serverName, storeDir) ;
    this.forwarder = forwarder ;
  }
  
  public void setFlushSize(int size) { this.flushSize = size ; }
  
  public void setForwarder(LogRequestForwarder forwarder) {
    this.forwarder = forwarder ;
  }
  
  @Override
  public void onTimerUpdate(String name, long timestampTick, long duration) {
    //System.out.println("plugin update: name=" + name +", timestamp=" + timestampTick +", duration=" + duration) ;
    TimerLog.Builder timerB = TimerLog.newBuilder() ;
    timerB.setName(name) ;
    timerB.setTimestampTick(timestampTick) ;
    timerB.setDuration(duration) ;
    timers.offer(timerB.build()) ;
    flush(timers.size(), timestampTick) ;
  }

  public void onCounterAdd(String name, long timestampTick, long incr) {
    CounterLog.Builder counterB = CounterLog.newBuilder() ;
    counterB.setName(name) ;
    counterB.setTimestampTick(timestampTick) ;
    counterB.setCount(incr) ;
    counters.offer(counterB.build()) ;
    flush(timers.size(), timestampTick) ;
  }

  private void flush(int size, long timestampTick) {
    if(size >= flushSize || timestampTick > lastFlushTime + SECOND_IN_NANO) {
      try {
        queue.writeObject(buildLogRequest() );
      } catch (Exception e) {
        //TODO: use logger to log the error
        e.printStackTrace();
      }
      lastFlushTime = timestampTick ;
    }
  }
  
  public void close() throws IOException {
    flush(flushSize + 1, Clock.defaultClock().getTick()) ;
    deamon.terminated = true ;
    deamon.interrupt(); 
  }
  
  private LogRequest buildLogRequest() {
    LogRequest.Builder reqB = LogRequest.newBuilder() ;
    reqB.setServer(serverName) ;
    List<CounterLog> counterHolder = new ArrayList<CounterLog>() ;
    counters.drainTo(counterHolder) ;
    reqB.addAllCounter(counterHolder) ;
    List<TimerLog> timerHolder = new ArrayList<TimerLog>() ;
    timers.drainTo(timerHolder) ;
    reqB.addAllTimer(timerHolder) ;
    return reqB.build() ;
  }
  
  public class ServerLogDeamon extends Thread {
    private long    waitOnError = 1 * 60 * 1000 ;
    private boolean error       = false ;
    private boolean terminated = false ;
    
    public ServerLogDeamon() {
      setDaemon(true) ;
    }

    synchronized void waitForTermination(long timeout) {
      try {
        wait(timeout) ;
      } catch (InterruptedException e) {
      }
    }
    
    synchronized void notifyTermination() {
      notifyAll() ;
    }
    
    public void run() {
      try {
        while(!terminated) {
          try {
            if(error) Thread.sleep(waitOnError);
            else Thread.sleep(5000);
          } catch (InterruptedException e1) {
            return ;
          }
          forward(10) ;
        }
      } finally {
        try {
          queue.close();
          forwarder.close() ;
        } catch (IOException e) {
          e.printStackTrace();
        } 
      }
    }
    
    void forward(long wait) {
      if(forwarder == null) return ;
      try {
        error = false ;
        Segment<LogRequest> segment = queue.nextReadSegment(wait) ;
        if(segment != null) {
          segment.open();
          while(segment.hasNext()) {
            LogRequest logRequest = segment.nextObject() ;
            forwarder.forward(logRequest);
          }
          queue.commitReadSegment(segment);
        }
      } catch (Exception e) {
        //TODO: use logger to log the request
        error = true ;
        System.err.println("ERROR ServerLogMetricPlugin: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }
}