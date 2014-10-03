package com.neverwinterdp.server.command;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.service.Service;
import com.neverwinterdp.server.service.ServiceRegistration;
import com.neverwinterdp.server.service.ServiceState;
import com.neverwinterdp.server.service.hello.HelloService;
import com.neverwinterdp.util.BeanInspector;
import com.neverwinterdp.yara.MetricRegistry;
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
    @Parameter(names = {"--cleanup"}, description = "Clean the all the data to get a clean environment")
    private boolean cleanup = false ;
    
    public ServiceRegistration execute(Server server, Service service) throws Exception {
      ServiceRegistration registration = service.getServiceRegistration() ;
      if(cleanup) service.cleanup();
      server.getModuleContainer().start(registration);
      return service.getServiceRegistration() ;
    }
  }
  
  static public class Restart extends ServiceCommand<ServiceRegistration> {
    @Parameter(names = {"--cleanup"}, description = "Clean the all the data to get a clean environment")
    private boolean cleanup ;
    
    public ServiceRegistration execute(Server server, Service service) throws Exception {
      server.getModuleContainer().stop(service.getServiceRegistration());
      if(cleanup) service.cleanup();
      server.getModuleContainer().start(service.getServiceRegistration());
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

  static public class Cleanup extends ServiceCommand<Boolean> {
    public Boolean execute(Server server, Service service) throws Exception {
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
  
  static public class GetServiceMetricRegistry extends ServiceCommand<MetricRegistry> {
    public MetricRegistry execute(Server server, Service service) throws Exception {
      MetricRegistry registry = new MetricRegistry(server.getClusterService().getMember().getMemberName()) ;
      registry.counter("TODO").incr() ;
      registry.timer("TODO").update(100, TimeUnit.NANOSECONDS);
      return registry ;
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
