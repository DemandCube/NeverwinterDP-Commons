package com.neverwinterdp.server.gateway.http;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.netty.http.HttpServer;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.gateway.ClusterGateway;
import com.neverwinterdp.server.gateway.CommandParams;
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
      "-Pserver.group=NeverwinterDP", "-Pserver.name=webserver", "-Pserver.roles=webserver"
    };
    instance = Server.create(args) ;
    gateway = new ClusterGateway() ;
    gateway.module.execute(
        "install", 
        new CommandParams().
          field("member-name", "webserver").
          field("autostart", true).
          field("module", new String[] { "cluster.gateway" }).
          field("-Phttp-www-dir", "src/main/webapp")
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