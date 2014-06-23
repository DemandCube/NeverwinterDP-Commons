package com.neverwinterdp.server.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;

import com.codahale.metrics.Timer;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.neverwinterdp.server.cluster.ClusterEvent;
import com.neverwinterdp.server.cluster.ClusterService;
import com.neverwinterdp.server.module.ModuleRegistration;
import com.neverwinterdp.server.module.ServiceModule;
import com.neverwinterdp.server.module.ModuleRegistration.RunningStatus;
import com.neverwinterdp.util.LoggerFactory;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

/**
 * @author Tuan Nguyen
 * @email tuan08@gmail.com
 */
public class ServiceContainer {
  @Inject
  private ApplicationMonitor registry;
  @Inject
  private ClusterService  clusterService;

  private Injector        container;
  private Logger          logger;
  private ModuleRegistration    moduleStatus ;
  private ServiceModule   module ;

  public ServiceModule getModule() { return this.module ; }
  
  public ModuleRegistration getModuleStatus() { return this.moduleStatus ; }
  
  public void init(ModuleRegistration mstatus, ServiceModule module, LoggerFactory lfactory) {
    this.moduleStatus = mstatus ;
    this.module = module ;
    logger = lfactory.getLogger(module.getClass().getSimpleName()) ;
  }

  public void install(Injector parentContainer) {
    uninstall(parentContainer) ;
    logger.info("install(Injector parentContainer)");
    container = parentContainer.createChildInjector(module) ;
    List<Binding<Service>> serviceBindings = container.findBindingsByType(TypeLiteral.get(Service.class));
    for (Binding<Service> sel : serviceBindings) {
      Service instance = container.getInstance(sel.getKey());
      Named named = (Named) sel.getKey().getAnnotation();
      ServiceRegistration sReg = instance.getServiceRegistration() ;
      sReg.setModule(moduleStatus.getModuleName());
      sReg.setServiceId(named.value());
      sReg.setClassName(instance.getClass().getName());
    }
    
    Map<Key<?>, Binding<?>> bindings = container.getBindings() ;
    for(Key<?> key : bindings.keySet()) {
      Object instance = container.getInstance(key) ;
      invokeAnnotatedMethod(instance, PostConstruct.class, new Object[] {}) ;
    }
    logger.info("Finish install(Injector parentContainer)");
  }
  
  public void uninstall(Injector parentContainer) {
    if(container == null) return ;
    Map<Key<?>, Binding<?>> bindings = container.getBindings() ;
    for(Key<?> key : bindings.keySet()) {
      Object instance = container.getInstance(key) ;
      invokeAnnotatedMethod(instance, PreDestroy.class, new Object[] {}) ;
    }
    this.container = null ;
  }
  
  public <T> T getInstance(Class<T> type) {
    return container.getInstance(type);
  }
  
  public void start() {
    if(moduleStatus.getRunningStatus().equals(RunningStatus.START)) return ;
    List<Binding<Service>> bindings = container.findBindingsByType(TypeLiteral.get(Service.class));
    for (Binding<Service> sel : bindings) {
      Service instance = container.getInstance(sel.getKey());
      start(instance);
    }
    clusterService.updateClusterRegistration(); 
    moduleStatus.setRunningStatus(RunningStatus.START);
  }

  private void start(Service service) {
    String serviceId = service.getServiceRegistration().getServiceId();
    Timer.Context timeCtx = registry.timer("service", serviceId, "start").time();
    logger.info("Start start(), service " + serviceId);
    try {
      service.start();
      service.getServiceRegistration().setState(ServiceState.START);
    } catch (Exception ex) {
      logger.error("Cannot launch the service " + serviceId, ex);
    } finally {
      timeCtx.stop();
    }
    clusterService.updateClusterRegistration(); 
    ClusterEvent event = new ClusterEvent();
    event.setType(ClusterEvent.ServiceStateChange);
    event.setSourceService(service.getServiceRegistration());
    event.setSource(service.getServiceRegistration().getState());    
    clusterService.broadcast(event) ;
    logger.info("Finish start(), service " + service.getServiceRegistration().getServiceId());
  }

  public void stop() {
    if(moduleStatus.getRunningStatus().equals(RunningStatus.STOP)) return ;
    List<Binding<Service>> bindings = container.findBindingsByType(TypeLiteral.get(Service.class));
    for (Binding<Service> sel : bindings) {
      Service instance = container.getInstance(sel.getKey());
      stop(instance);
    }
    clusterService.updateClusterRegistration(); 
    moduleStatus.setRunningStatus(RunningStatus.STOP);
  }

  private void stop(Service service) {
    String serviceId = service.getServiceRegistration().getServiceId();
    Timer.Context timeCtx = registry.timer("service", serviceId, "stop").time();
    logger.info("Start stop(), service " + serviceId);
    service.stop();
    service.getServiceRegistration().setState(ServiceState.STOP);
    clusterService.updateClusterRegistration(); 
    ClusterEvent event = new ClusterEvent();
    event.setType(ClusterEvent.ServiceStateChange);
    event.setSourceService(service.getServiceRegistration());
    event.setSource(service.getServiceRegistration().getState());    
    clusterService.broadcast(event);
    logger.info("Finish stop(), service " + serviceId);
    timeCtx.stop();
  }

  public void start(ServiceRegistration registration) {
    start(registration.getServiceId());
  }

  public void start(String serviceId) {
    Service service =
        container.getInstance(Key.get(Service.class, Names.named(serviceId)));
    if (ServiceState.START.equals(service.getServiceRegistration().getState())) {
      return;
    }
    start(service);
  }
  
  public void stop(ServiceRegistration registration) {
    stop(registration.getServiceId());
  }

  public void stop(String serviceId) {
    Service service =
        container.getInstance(Key.get(Service.class, Names.named(serviceId)));
    if (!ServiceState.START.equals(service.getServiceRegistration().getState())) {
      return;
    }
    stop(service);
  }

  public List<ServiceRegistration> getServiceRegistrations() {
    List<ServiceRegistration> holder = new ArrayList<ServiceRegistration>();
    List<Binding<Service>> bindings =
        container.findBindingsByType(TypeLiteral.get(Service.class));
    for (Binding<Service> sel : bindings) {
      Service instance = container.getInstance(sel.getKey());
      holder.add(instance.getServiceRegistration());
    }
    return holder;
  }

  /**
   * This method is used to find a specifice service by the service id
   * 
   * @param serviceId
   * @return
   */
  public <T extends Service > T getService(String serviceId) {
    T service =
        (T) container.getInstance(Key.get(Service.class, Names.named(serviceId)));
    return service;
  }

  /**
   * This method is used to find a specifice service by the service descriptor
   * 
   * @param registration
   * @return
   */
  public Service getService(ServiceRegistration registration) {
    return getService(registration.getServiceId());
  }

  public void collect(List<ServiceRegistration> holder) {
    List<Binding<Service>> bindings = container.findBindingsByType(TypeLiteral.get(Service.class));
    for (int i = 0; i < bindings.size(); i++) {
      Binding<Service> sel = bindings.get(i);
      holder.add(container.getInstance(sel.getKey()).getServiceRegistration());
    }
  }
  
  private <T extends Annotation> void invokeAnnotatedMethod(Object instance, Class<T> annotatedClass, Object[] args) {
    Method[] method = instance.getClass().getMethods() ;
    for(Method selMethod : method) {
      Annotation annotation = selMethod.getAnnotation(annotatedClass) ;
      if(annotation != null) {
        try {
          selMethod.invoke(instance, args) ;
        } catch (Exception e) {
          logger.warn("Cannot call " + selMethod.getName() + " for " + instance.getClass(), e);
        }
      }
    }
  }
}