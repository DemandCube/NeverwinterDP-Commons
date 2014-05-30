package com.neverwinterdp.guice;

import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class GuiceModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(Pojo.class);
    Properties properties = new Properties() ;
    properties.put("foo", "fooooooooooooo") ;
    properties.put("baz", "true") ;
    properties.put("integer", "100") ;
    properties.put("foo.baz", "foo.baz") ;
    properties.put("array", "value 1, value2") ;
    Names.bindProperties(binder(), properties) ;
    System.out.println("Call Module Configure.......................");
  }
}
