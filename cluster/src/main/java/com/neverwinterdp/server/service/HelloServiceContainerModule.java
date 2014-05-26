package com.neverwinterdp.server.service;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class HelloServiceContainerModule extends AbstractModule {
  @Override
  protected void configure() {  
    bind(key("HelloService")).to(HelloService.class).asEagerSingleton(); ;
  }
  
  private Key<Service> key(String serviceId) {
    return Key.get(Service.class, Names.named("HelloService")) ;
  }
}