package com.neverwinterdp.server.cluster.hazelcast;

import java.io.Serializable;
import java.util.concurrent.Callable;

import com.codahale.metrics.Timer;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.neverwinterdp.server.ActivityLog;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.command.ServiceCommand;
import com.neverwinterdp.server.service.Service;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
class ServiceCommandWrapper<T> implements Callable<T>, HazelcastInstanceAware, Serializable {
  transient private HazelcastInstance hzInstance ;
  private ServiceCommand<T> command ;
  
  public ServiceCommandWrapper() { }
  
  public ServiceCommandWrapper(ServiceCommand<T> command) {
    this.command = command ;
  }
  
  final public T call() throws Exception {
    HazelcastClusterService rpc = HazelcastClusterService.getClusterRPC(hzInstance) ;
    Server server = rpc.getServer() ;
    ApplicationMonitor registry = server.getApplicationMonitor() ;
    Timer.Context ctx =  
      registry.timer("services", "command", command.getActivityLogName()).time();
    T result = doExecute(server) ;
    ctx.stop();
    return result ;
  }
  
  final public T doExecute(Server server) throws Exception {
    long start = 0, end = 0 ;
    if(command.isLogEnable()) start = System.currentTimeMillis() ;
    server.getLogger().info("Start execute command " + command.getActivityLogName());
    String module = command.getTargetModule() ;
    String serviceId = command.getTargetServiceId() ;
    Service service = server.getModuleContainer().getService(module, serviceId) ;
    
    if(service == null) {
      throw new Exception("Service " + serviceId + " is not found on " + server.getServerRegistration().getServerName()) ;
    }
    
    T result = command.execute(server, service) ;
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