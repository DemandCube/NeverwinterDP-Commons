package com.neverwinterdp.server.command;

import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.service.Service;
import com.neverwinterdp.server.service.ServiceRegistration;
import com.neverwinterdp.server.service.ServiceState;
import com.neverwinterdp.util.BeanInspector;
import com.neverwinterdp.util.monitor.ComponentMonitorable;
import com.neverwinterdp.util.monitor.snapshot.ComponentMonitorSnapshot;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class ServiceCommands {
  static public class Ping extends ServiceCommand<ServiceState> {
    public ServiceState execute(Server server, Service service) throws Exception {
      return service.getServiceRegistration().getState() ;
    }
  }
  
  static public class Start extends ServiceCommand<ServiceRegistration> {
    public ServiceRegistration execute(Server server, Service service) throws Exception {
      service.start(); 
      return service.getServiceRegistration() ;
    }
  }
  
  static public class Restart extends ServiceCommand<ServiceRegistration> {
    @Parameter(names = {"--cleanup"}, description = "Clean the all the data to get a clean environment")
    private boolean cleanup ;
    
    public ServiceRegistration execute(Server server, Service service) throws Exception {
      service.stop();
      if(cleanup) service.cleanup();
      service.start(); 
      return service.getServiceRegistration() ;
    }
  }

  static public class Stop extends ServiceCommand<ServiceRegistration> {
    public ServiceRegistration execute(Server server, Service service) throws Exception {
      service.stop(); 
      return service.getServiceRegistration() ;
    }
  }

  static public class Cleanup extends ServiceCommand<Boolean> {
    public Boolean execute(Server server, Service service) throws Exception {
      ServiceRegistration registration = service.getServiceRegistration() ;
      return service.cleanup();
    }
  }
  
  static public class Configure extends ServiceCommand<Boolean> {
    @DynamicParameter(names = "-P", description = "Service properties")
    private Map<String, String> properties = new HashMap<String, String>();
    
    public Boolean execute(Server server, Service service) throws Exception {
      return service.configure(properties);
    }
  }
  
  static public class GetServiceMonitor extends ServiceCommand<ComponentMonitorSnapshot> {
    public ComponentMonitorSnapshot execute(Server server, Service service) throws Exception {
      if(service instanceof ComponentMonitorable) {
        return ((ComponentMonitorable)service).getComponentMonitor().getComponentMonitorSnapshot() ;
      }
      return null ;
    }
  }
  
  static public class MethodCall<T> extends ServiceCommand<T> {
    private String methodName ;
    private Object[] args ;
    
    public MethodCall() {} 
    
    public MethodCall(String methodName, Object ... args) {
      this.methodName = methodName ;
      this.args = args ;
    }
    
    public T execute(Server server, Service service) throws Exception {
      BeanInspector<Service> inspector = BeanInspector.get(service.getClass()) ;
      return (T) inspector.call(service, methodName, args) ;
    }
    
    public String getActivityLogName() { 
      return getTargetServiceId() + "/"  + methodName; 
    }
  }
}
