package com.neverwinterdp.server.service;

import java.io.Serializable;

/**
 * @author Tuan Nguyen
 * @email tuan08@gmail.com
 */
public class ServiceRegistration implements Serializable {
  private String       module ;
  private String       serviceId ;
  private String       className ;
  private String       name;
  private float        version;
  private String       description;
  private ServiceState state = ServiceState.INIT;
  
  public ServiceRegistration() {
  }

  public String getModule() { return this.module ; }
  public void   setModule(String module) { this.module = module ;}
  
  public String getServiceId() { return serviceId; }
  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getClassName() { return this.className ; }
  public void   setClassName(String className) { this.className = className ; }
  
  public float getVersion() {
    return version;
  }

  public void setVersion(float version) {
    this.version = version;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ServiceState getState() {
    return state;
  }

  public void setState(ServiceState state) {
    this.state = state;
  }
}
