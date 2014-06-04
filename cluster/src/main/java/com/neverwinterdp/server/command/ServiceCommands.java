package com.neverwinterdp.server.command;

import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.monitor.ComponentMonitorSnapshot;
import com.neverwinterdp.server.monitor.Monitorable;
import com.neverwinterdp.server.service.Service;
import com.neverwinterdp.server.service.ServiceRegistration;
import com.neverwinterdp.server.service.ServiceState;
import com.neverwinterdp.util.BeanInspector;
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
      ServiceRegistration registration = service.getServiceRegistration() ;
      server.getModuleContainer().start(registration);
      return service.getServiceRegistration() ;
    }
  }

  static public class Stop extends ServiceCommand<ServiceRegistration> {
    public ServiceRegistration execute(Server server, Service service) throws Exception {
      ServiceRegistration registration = service.getServiceRegistration() ;
      server.getModuleContainer().stop(registration);
      return service.getServiceRegistration() ;
    }
  }
  
  static public class GetServiceMonitor extends ServiceCommand<ComponentMonitorSnapshot> {
    public ComponentMonitorSnapshot execute(Server server, Service service) throws Exception {
      if(service instanceof Monitorable) {
        return ((Monitorable)service).getComponentMonitorRegistry().getComponentMonitorSnapshot() ;
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
