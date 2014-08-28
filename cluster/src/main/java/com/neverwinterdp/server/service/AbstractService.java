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

  /**
   * This method allow the client send a command with a properties parameter to reconfigure the service
   */
  public boolean configure(Map<String, String> properties) throws Exception{
    throw new Exception("This method should be overrided") ;
  }
  
  /**
   * This method is convienent to clean the data of the service in order to have a clean service state, convienient 
   * for testing
   */
  public boolean cleanup() throws Exception {
    return false ;
  }
  
  public void cleanRestart() throws Exception {
    stop() ;
    cleanup() ;
    start() ;
  }
}