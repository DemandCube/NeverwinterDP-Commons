package com.neverwinterdp.server.service;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.name.Names;

abstract public class ServiceModule extends AbstractModule {
  protected <T extends Service> void bind(String serviceId, Class<T> type) {
    Key<Service> key = Key.get(Service.class, Names.named(serviceId)) ;
    bind(key).to(type).asEagerSingleton(); ;
  }
  
  protected <T extends Service> void bind(String serviceId, Service instance) {
    Key<Service> key = Key.get(Service.class, Names.named(serviceId)) ;
    bind(key).toInstance(instance); ;
  }
}