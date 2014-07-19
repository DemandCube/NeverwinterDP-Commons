package com.neverwinterdp.server.service;

/**
 * @author Tuan Nguyen
 * @email tuan08@gmail.com
 */
abstract public class AbstractService implements Service {
  private ServiceRegistration registration = new ServiceRegistration();
  
  public ServiceRegistration getServiceRegistration() { return this.registration; }
  
  public void restart() throws Exception {
    stop() ;
    start() ;
  }
}