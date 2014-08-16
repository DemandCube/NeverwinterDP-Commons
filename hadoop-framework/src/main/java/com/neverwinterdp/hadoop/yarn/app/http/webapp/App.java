package com.neverwinterdp.hadoop.yarn.app.http.webapp;

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
  public App(String appId) {
    this.applicationId = appId ;
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

}
