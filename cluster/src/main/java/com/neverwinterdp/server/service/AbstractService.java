package com.neverwinterdp.server.service;

/**
 * @author Tuan Nguyen
 * @email tuan08@gmail.com
 */
abstract public class AbstractService implements Service {
  private ServiceRegistration registration = new ServiceRegistration();

  public String getServiceId() {
    return registration.getServiceId();
  }

  public void setServiceId(String id) {
    registration.setServiceId(id);
    ;
  }

  public ServiceRegistration getServiceRegistration() {
    return this.registration;
  }
}
