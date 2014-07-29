package com.neverwinterdp.server.command;

import java.io.Serializable;

import com.beust.jcommander.Parameter;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.service.Service;
import com.neverwinterdp.server.service.ServiceRegistration;

/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
abstract public class ServiceCommand<T> implements Serializable {
  private long    timeout = 5000l ; 
  private boolean logEnable ;
  @Parameter(names = {"--module"}, description = "Target the module")
  private String  targetModule ;
  @Parameter(names = {"--service-id"}, description = "Target the service id")
  private String  targetServiceId ;
  
  public ServiceCommand() {
  }
  
  public long getTimeout() { return timeout ; }
  public ServiceCommand<T> setTimeout(long timeout) {
    this.timeout = timeout ;
    return this ;
  }
  
  public boolean isLogEnable() { return logEnable; }
  public ServiceCommand<T>    setLogEnable(boolean logEnable) {
    this.logEnable = logEnable;
    return this ;
  }

  public String getTargetModule() { return this.targetModule ; }
  
  public String getTargetServiceId() { return this.targetServiceId ; }
  
  public void setTargetService(ServiceRegistration target) {
    this.targetModule = target.getModule() ;
    this.targetServiceId = target.getServiceId() ;
  }

  public void setTargetService(String module, String serviceId) {
    this.targetModule = module ;
    this.targetServiceId = serviceId ;
  }
  
  abstract public T execute(Server server, Service service)  throws Exception ;

  public String getActivityLogName() { return getClass().getSimpleName() ; }
  
  public String getActivityLogMessage() { return null ; }
}
