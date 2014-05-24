package com.neverwinterdp.server.cluster.hazelcast;

import java.io.Serializable;
import java.util.concurrent.Callable;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.neverwinterdp.server.ActivityLog;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.command.ServerCommand;
import com.neverwinterdp.util.monitor.MonitorRegistry;

/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
class ServerCommandWrapper<T> implements Callable<T>, HazelcastInstanceAware, Serializable {
  transient private HazelcastInstance hzInstance ;
  private ServerCommand<T> command ;
  
  public ServerCommandWrapper() { }
  
  public ServerCommandWrapper(ServerCommand<T> command) {
    this.command = command ;
  }
  
  final public T call() throws Exception {
    HazelcastCluster rpc = HazelcastCluster.getClusterRPC(hzInstance) ;
    Server server = rpc.getServer() ;
    MonitorRegistry registry = server.getMonitorRegistry() ;
    Timer.Context ctx =  
      registry.timer("command", command.getActivityLogName()).time();
    T result = doExecute(server) ;
    ctx.stop();
    return result ;
  }
  
  
  private T doExecute(Server server) throws Exception {
    long start = 0, end = 0 ;
    if(command.isLogEnable()) start = System.currentTimeMillis() ;
    server.getLogger().info("Start execute command " + command.getActivityLogName());
    T result = command.execute(server) ;
    if(command.isLogEnable()) {
      end = System.currentTimeMillis() ;
      String name = command.getActivityLogName(); 
      String msg = command.getActivityLogMessage() ;
      ActivityLog log = new ActivityLog(name, ActivityLog.Type.Command, start, end, msg) ;
      server.getActivityLogs().add(log);
    }
    server.getLogger().info("Finish execute command " + command.getActivityLogName());
    return result ;
  }
  
  public void setHazelcastInstance(HazelcastInstance hzInstance) {
    this.hzInstance = hzInstance ;
  }
}