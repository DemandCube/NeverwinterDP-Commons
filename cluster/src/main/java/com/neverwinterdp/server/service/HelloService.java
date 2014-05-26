package com.neverwinterdp.server.service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;

import com.codahale.metrics.Counter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.neverwinterdp.util.LoggerFactory;
import com.neverwinterdp.util.monitor.MonitorRegistry;
import com.neverwinterdp.util.monitor.Monitorable;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
@Singleton
public class HelloService extends AbstractService implements Monitorable  {
  private Logger logger ;
  @Inject(optional = true) @Named("server.group")
  private String   serverGroup;
  
  @Inject private MonitorRegistry monitorRegistry ;
  private Counter helloCounter ;

  public String getServerGroup() { return this.serverGroup ; }
  
  public MonitorRegistry getMonitorRegistry() { return this.monitorRegistry ; }
  
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
    logger.info("Stopping the HelloService......................");
    logger.info("Finish stop()");
  }

  public String hello(String message) {
    helloCounter.inc();
    return "Hello " + message;
  }
}
