package com.neverwinterdp.hadoop.yarn.app.http.webapp;

import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.WebApp;

import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;

public class AMWebApp extends WebApp {
  private AppMaster appMaster  ;
  
  public AMWebApp(AppMaster appMaster) {
    this.appMaster = appMaster ;
  }
  
  @Override
  public void setup() {
    bind(AppMaster.class).toInstance(appMaster) ;
    
    bind(JAXBContextResolver.class);
    bind(GenericExceptionHandler.class);
    bind(AMWebServices.class);
    
    route("/", AppController.class);
    route("/monitor", AppController.class, "monitor");
  }

}
