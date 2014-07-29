package com.neverwinterdp.server.service;

import java.util.Map;

/**
 * @author Tuan Nguyen
 * @email tuan08@gmail.com
 */
abstract public class AbstractService implements Service {
  private ServiceRegistration registration = new ServiceRegistration();
  
  public ServiceRegistration getServiceRegistration() { return this.registration; }
  
  public ServiceInfo getServiceInfo() { return null ; }
  
  public void restart() throws Exception {
    stop() ;
    start() ;
  }

  public boolean configure(Map<String, String> properties) throws Exception{
    throw new Exception("This method should be overrided") ;
  }
  
  public boolean cleanup() throws Exception {
    return false ;
  }
  
  public void cleanRestart() throws Exception {
    stop() ;
    cleanup() ;
    start() ;
  }
}