package com.neverwinterdp.server.service;

import java.util.Map;

/**
 * @author Tuan Nguyen
 * @email tuan08@gmail.com
 * 
 *        This is a service or a service wrapper to another project such
 *        zookeeper , kafka, Vertx...
 */
public interface Service {
  /**
   * The service descriptor contain the information of the service such name ,
   * version , the state of the service so another service or remote service can
   * decide to use this service or not.
   * 
   * @return
   */
  public ServiceRegistration getServiceRegistration();

  public <T extends ServiceInfo> T getServiceInfo() ;
  
  public boolean configure(Map<String, String> properties) throws Exception;
  
  public boolean cleanup() throws Exception;
  
  /**
   * This method is designed to start the service and change the service state
   * to START. If the service is a wrapper to another service such zookeeper,
   * kafka... All the real service state such load, configFile, init, start
   * should be implemented in this method
   */
  public void start() throws Exception;

  /**
   * This method is designed to stop the service and change the service state to
   * STOP. If the service is a wrapper to another service such zookeeper,
   * kafka... All the real service state such stop, destroy should be
   * implemented in this method
   */
  public void stop();
}