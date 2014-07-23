package com.neverwinterdp.server.service.hello;

import java.util.Map;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.neverwinterdp.server.service.AbstractService;
import com.neverwinterdp.util.LoggerFactory;
import com.neverwinterdp.util.monitor.ApplicationMonitor;
import com.neverwinterdp.util.monitor.ComponentMonitor;
import com.neverwinterdp.util.monitor.ComponentMonitorable;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class HelloService extends AbstractService implements ComponentMonitorable  {
  private Logger logger ;
  
  @Inject @Named("hello")
  private String   helloProperty;
  
  @Inject(optional=true) @Named("helloProperties")
  private Map<String, String>   helloProperties;
  
  
  @Inject(optional = true) @Named("server.group")
  private String   serverGroup;
  
  private ComponentMonitor monitorRegistry ;

  public String getServerGroup() { return this.serverGroup ; }
  
  public String getHelloProperty() { return helloProperty ; }
  
  public Map<String, String> getHelloProperties() {
    return this.helloProperties ;
  }
  
  @Inject
  public void setLoggerFactory(LoggerFactory lfactory) {
    logger = lfactory.getLogger("HelloService") ;
  }
  
  @Inject
  public void init(ApplicationMonitor mRegistry) {
    this.monitorRegistry = mRegistry.createComponentMonitor("HelloModule", getClass().getSimpleName()) ;
  }

  public ComponentMonitor getComponentMonitor() { 
    return this.monitorRegistry ; 
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
    Timer helloTimer = monitorRegistry.timer("hello-counter") ;
    Timer.Context ctx = helloTimer.time() ;
    Counter helloCounter = monitorRegistry.counter("hello-timer") ;
    helloCounter.inc();
    ctx.stop() ;
    return "Hello " + message;
  }
}