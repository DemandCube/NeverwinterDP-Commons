package com.neverwinterdp.server.yara;

import java.io.IOException;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.neverwinterdp.netty.rpc.client.RPCClient;
import com.neverwinterdp.server.service.AbstractService;
import com.neverwinterdp.util.LoggerFactory;
import com.neverwinterdp.yara.MetricRegistry;
import com.neverwinterdp.yara.client.RPCLogRequestForwarder;
import com.neverwinterdp.yara.client.ServerLogMetricPlugin;

public class YaraClientService extends AbstractService {
  private Logger logger ;
  private YaraConfig config ;
  private MetricRegistry metricRegistry ;
  
  @Inject
  public void init(LoggerFactory lfactory, MetricRegistry mRegistry, YaraConfig config) {
    logger = lfactory.getLogger(YaraClientService.class) ;
    logger.info("Start init(...)");
    this.metricRegistry = mRegistry ;
    this.config = config ;
    logger.info("Finish init(...)");
  }
  
  public void start() throws Exception {
    logger.info("Start start(...)");
    RPCClient client = new RPCClient(config.getRpcHost(), config.getRpcPort()) ;
    RPCLogRequestForwarder forwarder = new RPCLogRequestForwarder(client) ;
    ServerLogMetricPlugin plugin = 
      new ServerLogMetricPlugin(config.getServerName(), config.getClientDataBufferDir(), forwarder) ;
    metricRegistry.getPluginManager().add(plugin);
    logger.info("Finish start(...)");
  }

  public void stop() {
    logger.info("Start stop(...)"); 
    ServerLogMetricPlugin plugin = metricRegistry.getPluginManager().remove(ServerLogMetricPlugin.class) ;
    if(plugin != null) {
      try {
        plugin.close();
      } catch (IOException e) {
        logger.error("ServerLogMetricPlugin close error", e);
      }
    }
    logger.info("Finish stop(...)");
  }

}
