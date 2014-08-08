package com.neverwinterdp.hadoop.yarn.app.hello.webapp;

import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.WebApp;

public class AMWebApp extends WebApp  {
  
  @Override
  public void setup() {
    bind(GenericExceptionHandler.class);
    bind(AMWebServices.class);
    route("/", AppController.class);
    route("/info", AppController.class);
  }
}