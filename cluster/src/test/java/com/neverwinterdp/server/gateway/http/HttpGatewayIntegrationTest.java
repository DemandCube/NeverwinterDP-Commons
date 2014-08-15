package com.neverwinterdp.server.gateway.http;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.netty.http.HttpServer;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.gateway.ClusterGateway;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class HttpGatewayIntegrationTest {
  static {
    System.setProperty("app.dir", "build/cluster") ;
    System.setProperty("log4j.configuration", "file:src/test/resources/log4j.properties") ;
  }
  
  static protected Server   instance ;
  static HttpServer httpServer ;
  static ClusterGateway gateway ;
  
  @BeforeClass
  static public void setup() throws Exception {
    String[] args = {
      "-Pserver.group=NeverwinterDP", "-Pserver.name=generic", "-Pserver.roles=generic"
    };
    instance = Server.create(args) ;
    gateway = new ClusterGateway() ;
    gateway.execute(
        "module install " +
        " -Phttp:port=8080" +
        " -Phttp:route.names=cluster-rest" +
        " -Phttp:route.cluster-rest.handler=com.neverwinterdp.server.gateway.http.HttpGatewayRouteHandler" +
        " -Phttp:route.cluster-rest.path=/cluster/rest" +
        " --member-name webserver --autostart --module Http"
    ) ;
  }

  @AfterClass
  static public void teardown() throws Exception {
    gateway.close(); 
    instance.exit(0) ;
  }
  
  @Test
  public void run() throws Exception {
    Thread.sleep(100000000);
  }
}