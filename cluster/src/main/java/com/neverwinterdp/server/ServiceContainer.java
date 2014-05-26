package com.neverwinterdp.server;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
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
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.neverwinterdp.server.cluster.ClusterEvent;
import com.neverwinterdp.server.cluster.ClusterService;
import com.neverwinterdp.server.service.HelloServiceContainerModule;
import com.neverwinterdp.server.service.Service;
import com.neverwinterdp.server.service.ServiceRegistration;
import com.neverwinterdp.server.service.ServiceState;
import com.neverwinterdp.util.LoggerFactory;
import com.neverwinterdp.util.monitor.MonitorRegistry;

/**
 * @author Tuan Nguyen
 * @email tuan08@gmail.com
 */
@Singleton
public class ServiceContainer {
  @Inject
  private MonitorRegistry registry;
  @Inject
  private ClusterService  clusterService;

  @Inject(optional = true)
  @Named("server.service-container-module")
  private String          serviceModuleContainer = HelloServiceContainerModule.class.getName();

  private Injector        container;
  private Logger          logger;

  @Inject
  public ServiceContainer(Injector parentContainer,
      LoggerFactory lfactory) throws Exception {
    logger = lfactory.getLogger("ServiceContainer");
    Class<Module> clazz = (Class<Module>) Class.forName(serviceModuleContainer);
    container = parentContainer.createChildInjector(clazz.newInstance());
  }

  public void onInit() {
    logger.info("Start onInit()");
    List<Binding<Service>> serviceBindings =
        container.findBindingsByType(TypeLiteral.get(Service.class));
    for (Binding<Service> sel : serviceBindings) {
      Service instance = container.getInstance(sel.getKey());
      Named named = (Named) sel.getKey().getAnnotation();
      instance.setServiceId(named.value());
    }
    Map<Key<?>, Binding<?>> bindings = container.getBindings() ;
    for(Key<?> key : bindings.keySet()) {
      Object instance = container.getInstance(key) ;
      Method[] method = instance.getClass().getMethods() ;
      for(Method selMethod : method) {
        Annotation annotation = selMethod.getAnnotation(PostConstruct.class) ;
        if(annotation != null) {
          try {
            selMethod.invoke(instance, new Object[] {}) ;
          } catch (Exception e) {
            logger.warn("Cannot call " + selMethod.getName() + " for " + instance.getClass(), e);
          }
        }
      }
    }
    logger.info("Finish onInit()");
  }

  public void onDestroy() {
    Map<Key<?>, Binding<?>> bindings = container.getBindings() ;
    for(Key<?> key : bindings.keySet()) {
      Object instance = container.getInstance(key) ;
      Method[] method = instance.getClass().getMethods() ;
      for(Method selMethod : method) {
        Annotation annotation = selMethod.getAnnotation(PreDestroy.class) ;
        if(annotation != null) {
          try {
            selMethod.invoke(instance, new Object[] {}) ;
          } catch (Exception e) {
            logger.warn("Cannot call " + selMethod.getName() + " for " + instance.getClass(), e);
          }
        }
      }
    }
  }

  public <T> T getInstance(Class<T> type) {
    return container.getInstance(type);
  }

  public void start() {
    List<Binding<Service>> bindings = container.findBindingsByType(TypeLiteral.get(Service.class));
    for (Binding<Service> sel : bindings) {
      Service instance = container.getInstance(sel.getKey());
      start(instance);
    }
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
    logger.info("Finish start(), service " + service.getServiceRegistration().getServiceId());
  }

  public void stop() {
    List<Binding<Service>> bindings = container.findBindingsByType(TypeLiteral.get(Service.class));
    for (Binding<Service> sel : bindings) {
      Service instance = container.getInstance(sel.getKey());
      stop(instance);
    }
  }

  private void stop(Service service) {
    String serviceId = service.getServiceRegistration().getServiceId();
    Timer.Context timeCtx = registry.timer("service", serviceId, "stop").time();
    logger.info("Start stop(), service " + serviceId);
    service.stop();
    service.getServiceRegistration().setState(ServiceState.STOP);
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
    ClusterEvent event = new ClusterEvent();
    event.setType(ClusterEvent.ServiceStateChange);
    event.setSourceService(service.getServiceRegistration());
    event.setSource(service.getServiceRegistration().getState());
    clusterService.broadcast(event);
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

    ClusterEvent event = new ClusterEvent();
    event.setType(ClusterEvent.ServiceStateChange);
    event.setSourceService(service.getServiceRegistration());
    event.setSource(service.getServiceRegistration().getState());
    clusterService.broadcast(event);
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
  public Service getService(String serviceId) {
    Service service =
        container.getInstance(Key.get(Service.class, Names.named(serviceId)));
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

  public Service[] getServices() {
    List<Binding<Service>> bindings = container.findBindingsByType(TypeLiteral.get(Service.class));
    Service[] services = new Service[bindings.size()];
    for (int i = 0; i < bindings.size(); i++) {
      Binding<Service> sel = bindings.get(i);
      services[i] = container.getInstance(sel.getKey());
    }
    return services;
  }
}
