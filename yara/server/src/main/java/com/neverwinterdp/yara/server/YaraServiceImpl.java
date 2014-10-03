package com.neverwinterdp.yara.server;

import java.util.List;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.neverwinterdp.yara.protocol.CounterLog;
import com.neverwinterdp.yara.protocol.LogRequest;
import com.neverwinterdp.yara.protocol.LogResponse;
import com.neverwinterdp.yara.protocol.TimerLog;
import com.neverwinterdp.yara.protocol.YaraService;
import com.neverwinterdp.yara.cluster.ClusterMetricRegistry;

public class YaraServiceImpl implements YaraService.BlockingInterface, YaraService.Interface {
  private ClusterMetricRegistry registry = new ClusterMetricRegistry();
  
  public ClusterMetricRegistry getClusterMetricRegistry() { return this.registry ; }
  
  public LogResponse log(RpcController controller, LogRequest request) throws ServiceException {
    LogResponse.Builder respB = LogResponse.newBuilder() ;
    respB.setLogTime(System.currentTimeMillis()) ;
    
    String serverName = request.getServer() ;
    List<TimerLog> timers = request.getTimerList() ;
    for(int i = 0; i < timers.size(); i++) {
      TimerLog tLog = timers.get(i) ;
      registry.timer(tLog.getName()).update(serverName, tLog.getName(), tLog.getTimestampTick(), tLog.getDuration());
    }
    respB.setTimerCount(timers.size()) ;
    
    List<CounterLog> counters = request.getCounterList() ; ;
    for(int i = 0; i < counters.size(); i++) {
      CounterLog cLog = counters.get(i) ;
      registry.counter(cLog.getName()).update(serverName, cLog.getTimestampTick(), cLog.getCount());
    }
    respB.setCounterCount(counters.size()) ;
    return respB.build() ;
  }

  public void log(RpcController controller, LogRequest request, RpcCallback<LogResponse> done) {
    try {
      LogResponse response = log(controller, request) ;
      done.run(response);
    } catch (ServiceException e) {
      controller.setFailed(e.getMessage());
      done.run(null);
    }
  }
}
