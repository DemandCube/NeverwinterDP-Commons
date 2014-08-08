package com.neverwinterdp.hadoop.yarn.app.hello.webapp;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
public class App {
  final AppContext context;

  @Inject
  App(AppContext ctx) {
    context = ctx;
  }
  
  public AppContext getAppContext() { return this.context ; }
}
