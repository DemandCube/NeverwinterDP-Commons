package com.neverwinterdp.server.service;

import java.util.Map;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;

import com.codahale.metrics.Counter;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.neverwinterdp.util.LoggerFactory;
import com.neverwinterdp.util.monitor.MonitorRegistry;
import com.neverwinterdp.util.monitor.Monitorable;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class HelloService extends AbstractService implements Monitorable  {
  private Logger logger ;
  
  @Inject @Named("hello")
  private String   helloProperty;
  
  @Inject(optional=true) @Named("helloProperties")
  private Map<String, String>   helloProperties;
  
  
  @Inject(optional = true) @Named("server.group")
  private String   serverGroup;
  
  @Inject private MonitorRegistry monitorRegistry ;
  private Counter helloCounter ;

  public String getServerGroup() { return this.serverGroup ; }
  
  public MonitorRegistry getMonitorRegistry() { return this.monitorRegistry ; }
  
  public String getHelloProperty() { return helloProperty ; }
  
  public Map<String, String> getHelloProperties() {
    return this.helloProperties ;
  }
  
  @Inject
  public void setLoggerFactory(LoggerFactory lfactory) {
    logger = lfactory.getLogger("HelloService") ;
  }
  
  @Inject
  public void init(MonitorRegistry mRegistry) {
    reset(mRegistry) ;
  }

  public void reset(MonitorRegistry mRegistry) {
    helloCounter = mRegistry.counter("service", "HelloService", "hello") ;
  }

  @PreDestroy
  public void onDestroy() {
    logger.info("onDestroy()");
  }
  
  public void start() {
    logger.info("Start start()");
    logger.info("Activating the HelloService...................");
    logger.info("Finish start()");
  }

  public void stop() {
    logger.info("Start stop()");
    System.out.println("==> helloProperties " + helloProperties) ;
    logger.info("Stopping the HelloService......................");
    logger.info("Finish stop()");
  }

  public String hello(String message) {
    helloCounter.inc();
    return "Hello " + message;
  }
}
