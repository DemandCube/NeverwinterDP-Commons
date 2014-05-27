package com.neverwinterdp.server.service;

import java.io.Serializable;

/**
 * @author Tuan Nguyen
 * @email tuan08@gmail.com
 */
public class ServiceRegistration implements Serializable {
  private String       serviceId ;
  private String       name;
  private float        version;
  private String       description;
  private ServiceState state = ServiceState.INIT;

  public ServiceRegistration() {
  }

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
