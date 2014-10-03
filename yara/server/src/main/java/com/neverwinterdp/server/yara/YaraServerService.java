package com.neverwinterdp.server.yara;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.neverwinterdp.netty.rpc.server.RPCServer;
import com.neverwinterdp.server.service.AbstractService;
import com.neverwinterdp.util.LoggerFactory;
import com.neverwinterdp.yara.protocol.YaraService;
import com.neverwinterdp.yara.server.YaraServiceImpl;
import com.neverwinterdp.yara.snapshot.ClusterMetricRegistrySnapshot;

public class YaraServerService extends AbstractService {
  private Logger     logger;
  private YaraConfig config;
  private RPCServer rpcServer;
  private YaraServiceImpl yaraService ;
  @Inject
  public void init(LoggerFactory lfactory, YaraConfig config) {
    logger = lfactory.getLogger(YaraServerService.class) ;
    logger.info("Start init(...)");
    this.config = config ;
    rpcServer = new RPCServer(config.getRpcPort()) ;
    rpcServer.setLogger(lfactory) ;
    yaraService = new YaraServiceImpl() ;
    logger.info("Finish init(...)");
  }
  
  public ClusterMetricRegistrySnapshot getClusterMetricRegistrySnapshot() {
    return new ClusterMetricRegistrySnapshot(yaraService.getClusterMetricRegistry()) ;
  }
  
  public void start() throws Exception {
    logger.info("Start start(...)");
    rpcServer.getServiceRegistry().register(YaraService.newReflectiveBlockingService(yaraService));
    rpcServer.startAsDeamon(); 
    logger.info("Finish start(...)");
  }

  public void stop() {
    logger.info("Start stop(...)");
    if(rpcServer != null) {
      rpcServer.shutdown();
      rpcServer = null ;
    }
    logger.info("Finish stop(...)");
  }

}
