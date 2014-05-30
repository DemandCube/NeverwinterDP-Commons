package com.neverwinterdp.guice.child;

import com.google.inject.AbstractModule;

public class ChildModule extends AbstractModule {
  protected void configure() {
    bind(ChildService.class).asEagerSingleton() ;
  }
}
