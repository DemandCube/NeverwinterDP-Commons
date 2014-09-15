package com.neverwinterdp.server.service.hello;

import java.util.Map;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.neverwinterdp.server.service.AbstractService;
import com.neverwinterdp.util.LoggerFactory;
import com.neverwinterdp.yara.Counter;
import com.neverwinterdp.yara.MetricRegistry;
import com.neverwinterdp.yara.Timer;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class HelloService extends AbstractService {
  private Logger logger ;
  
  @Inject @Named("hello")
  private String   helloProperty;
  
  @Inject(optional=true) @Named("helloProperties")
  private Map<String, String>   helloProperties;
  
  
  @Inject(optional = true) @Named("server.group")
  private String   serverGroup;
  
  private MetricRegistry monitorRegistry ;

  public String getServerGroup() { return this.serverGroup ; }
  
  public String getHelloProperty() { return helloProperty ; }
  
  public Map<String, String> getHelloProperties() {
    return this.helloProperties ;
  }
  
  @Inject
  public void init(LoggerFactory lfactory, MetricRegistry mRegistry) {
    logger = lfactory.getLogger("HelloService") ;
    this.monitorRegistry = mRegistry ;
  }

  public MetricRegistry getComponentMonitor() { 
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
    Timer helloTimer = monitorRegistry.timer("hello-timer") ;
    Timer.Context ctx = helloTimer.time() ;
    Counter counter = monitorRegistry.counter("hello-counter") ;
    counter.incr();
    ctx.stop() ;
    return "Hello " + message;
  }
}