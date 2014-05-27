package com.neverwinterdp.server;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.neverwinterdp.server.cluster.ClusterEvent;
import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.server.cluster.ClusterService;
import com.neverwinterdp.server.service.ServiceRegistration;
import com.neverwinterdp.util.LoggerFactory;
import com.neverwinterdp.util.monitor.MonitorRegistry;
/**
 * @author Tuan Nguyen
 * @email tuan08@gmail.com
 * 
 * The server can be understood as a single container or a machine that
 * contains an unlimited number of the services.
 */
@Singleton
public class Server {
  private Logger           logger;
  
  @Inject
  private LoggerFactory    loggerFactory ;

  @Inject
  private ServerConfig     config;
  
  @Inject
  private RuntimeEnvironment runtimeEnvironment ; 
  
  @Inject
  private ClusterService   cluster;
  
  @Inject
  private ServiceContainer serviceContainer;
  
  private ActivityLogs     activityLogs = new ActivityLogs();
  private ServerState      serverState  = null;
  
  @Inject
  private MonitorRegistry monitorRegistry ;
  
  /**
   * The configuration for the server such name, group, version, description,
   * listen port. It also may contain the service configurations
   * */
  public ServerConfig getConfig() {
    return config;
  }

  public ClusterService getClusterService() {
    return cluster;
  }

  public ServerState getServerState() {
    return serverState;
  }

  public void setServerState(ServerState state) {
    this.serverState = state;
  }

  public ActivityLogs getActivityLogs() {
    return this.activityLogs;
  }

  public ServiceContainer getServiceContainer() { return serviceContainer ; }

  public ServerRegistration getServerRegistration() {
    List<ServiceRegistration> list = serviceContainer.getServiceRegistrations();
    ClusterMember member = cluster.getMember();
    return new ServerRegistration(member, serverState, list);
  }

  public Logger getLogger() {
    return this.logger;
  }
  
  public LoggerFactory getLoggerFactory() {
    return this.loggerFactory ;
  }

  public RuntimeEnvironment getRuntimeEnvironment() {
    return this.runtimeEnvironment ; 
  }

  public MonitorRegistry getMonitorRegistry() { return this.monitorRegistry ; }
  
  /**
   * This lifecycle method be called after the configuration is set. The method
   * should: 1. Compute the configuration and add the services with the service
   * configuration. 2. Loop through the services and call service.onInit() 3.
   * Set the state of the services to init. 4. Set the state of the server to
   * init. 5. Add and configure the cluster services, start the cluster services
   */
  public void onInit() {
    long start = System.currentTimeMillis();
    setServerState(ServerState.INIT);
    logger = loggerFactory.getLogger("Server");
    logger.info("Start onInit()");
    serviceContainer.onInit();
    cluster.onInit(this);
    long end = System.currentTimeMillis();
    activityLogs.add(new ActivityLog("Init", ActivityLog.Type.Auto, start, end, null));
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
       Server.this.exit(0);
      }
    });
    logger.info("Finish onInit()");
  }

  /**
   * This method is called after the stopServices is called. This method should:
   * 1. Stop and destroy all the cluster services 2. Release all the resources
   * if necessary, save the monitor or profile information.
   */
  public void onDestroy() {
    if(ServerState.EXIT.equals(getServerState())) {
      return ;
    }
    logger.info("Start onDestroy()");
    serviceContainer.onDestroy();
    serviceContainer = null;
    cluster.onDestroy(this);
    setServerState(ServerState.EXIT) ;
    logger.info("Finish onDestroy()");
  }

  /**
   * This method lifecycle should be called after the method onInit() is called.
   * This method should be called by the external service that can access the
   * Server instance or the cluster services.
   * 
   * This method should: 1. Check the state of the server, if the state of the
   * server is already START, then return 2. Loop through all the services, call
   * service.start().
   */
  public void start() {
    if(ServerState.RUNNING.equals(getServerState()) ||
       ServerState.EXIT.equals(getServerState())) {
      return ;
    }
    logger.info("Start start()");
    serviceContainer.start();
    setServerState(ServerState.RUNNING);
    cluster.getClusterRegistration().update(getServerRegistration());
    ClusterEvent clusterEvent = new ClusterEvent(ClusterEvent.ServerStateChange, getServerState());
    cluster.broadcast(clusterEvent);
    logger.info("Finish start()");
  }

  /**
   * This method is used to stop all the services usually it is used to
   * simmulate the server shutdown or suspend.
   */
  public void shutdown() {
    if(ServerState.SHUTDOWN.equals(getServerState()) ||
       ServerState.EXIT.equals(getServerState())) {
      return ;
    }
    logger.info("Start shutdown()");
    cluster.getClusterRegistration().remove(cluster.getMember());
    serviceContainer.stop();
    setServerState(ServerState.SHUTDOWN);
    ClusterEvent clusterEvent = new ClusterEvent(ClusterEvent.ServerStateChange, getServerState());
    cluster.broadcast(clusterEvent);
    logger.info("Finish shutdown()");
  }
  
  public void exit(long wait) {
    if(wait <= 0) {
      //exit immediately
      shutdown();
      onDestroy() ;
    } else {
      //exit in wait ms
      ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
      Runnable shutdownTask = new Runnable() {
        public void run() {
          shutdown();
          onDestroy() ;
        }
      };
      worker.schedule(shutdownTask, wait, TimeUnit.MILLISECONDS);
    }
  }
  
  static public Server create(Properties properties) {
    Injector container = null ;
    if(properties == null) {
      container = Guice.createInjector(new ServerModule());
    } else {
      container = Guice.createInjector(new ServerModule(properties));
    }
    Server server = container.getInstance(Server.class) ;
    server.onInit() ;
    server.start();
    return server ;
  }
}