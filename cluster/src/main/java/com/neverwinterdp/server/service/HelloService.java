package com.neverwinterdp.server.service;

import com.codahale.metrics.Counter;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.util.monitor.MonitorRegistry;
import com.neverwinterdp.util.monitor.Monitorable;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class HelloService extends AbstractService implements Monitorable  {
  private Counter helloCounter ;
  
  public void onInit(Server server) {
    super.onInit(server);
  }
  
  public void start() {
    logger.info("Start start()");
    logger.info("Activating the HelloService...................");
    logger.info("Finish start()");
  }

  public void stop() {
    logger.info("Start stop()");
    logger.info("Stopping the HelloService......................");
    logger.info("Finish stop()");
  }

  public String hello(String message) {
    helloCounter.inc();
    return "Hello " + message;
  }
  
  public void init(MonitorRegistry mRegistry) {
    reset(mRegistry) ;
  }

  public void reset(MonitorRegistry mRegistry) {
    helloCounter = mRegistry.counter("service", "HelloService", "hello") ;
  }
}
