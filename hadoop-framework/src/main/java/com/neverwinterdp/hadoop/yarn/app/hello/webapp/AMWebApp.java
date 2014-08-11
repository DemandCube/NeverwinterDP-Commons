package com.neverwinterdp.hadoop.yarn.app.hello.webapp;

import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.WebApp;

import com.google.inject.Key;
import com.google.inject.name.Names;

public class AMWebApp extends WebApp  {
  AppContext appContext ;
  
  public AMWebApp(AppContext appContext) {
    this.appContext = appContext ;
  }
  
  @Override
  public void setup() {
    Key<AppContext> key = Key.get(AppContext.class, Names.named(AppContext.class.getSimpleName())) ;
    bind(key).toInstance(appContext) ;
    
    bind(JAXBContextResolver.class);
    bind(GenericExceptionHandler.class);
    bind(AMWebServices.class);
    
    route("/", AppController.class);
    route("/info", AppController.class, "info");
  }
}