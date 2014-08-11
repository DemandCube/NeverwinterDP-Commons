package com.neverwinterdp.hadoop.yarn.app.hello.webapp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.inject.Inject;

@XmlRootElement(name = "app")
@XmlAccessorType(XmlAccessType.FIELD)
public class App {
  
  protected String applicationId = "App";
  
  public App() {}
  
  @Inject
  public App(AppContext ctx) {
    this.applicationId = ctx.getApplicationId() ;
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

}
