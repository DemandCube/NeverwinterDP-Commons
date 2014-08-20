package com.neverwinterdp.server.service;

import java.util.Map;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.neverwinterdp.netty.http.HttpServer;
import com.neverwinterdp.server.RuntimeEnvironment;
import com.neverwinterdp.util.LoggerFactory;

public class HttpService extends AbstractService {
  private Logger logger ;
  private HttpServer server ;
  
  @Inject
  public void init(RuntimeEnvironment rtEnv,
                   LoggerFactory factory,
                   @Named("httpProperties") Map<String, String> httpProperties) throws Exception {
    logger = factory.getLogger(getClass().getSimpleName()) ;
    logger.info("Start init(...)");
    server = new HttpServer();
    server.setLoggerFactory(factory) ;
    server.configure(httpProperties) ;
    logger.info("Finish init(..)");
  }
  
  public void start() throws Exception {
    logger.info("Start start()");
    logger.info("Http listen port = " + server.getPort()) ;
    
    server.startAsDeamon();
    logger.info("Finish start()");
  }

  public void stop() {
    logger.info("Start stop() hashcode = " + hashCode());
    server.shutdown();
    logger.info("Finish stop()");
  }
}