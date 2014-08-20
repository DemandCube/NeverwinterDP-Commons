package com.neverwinterdp.server.gateway.http;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.netty.http.HttpServer;
import com.neverwinterdp.netty.http.client.DumpResponseHandler;
import com.neverwinterdp.netty.http.client.AsyncHttpClient;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.gateway.ClusterGateway;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class HttpGatewayUnitTest {
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
  public void testPing() throws Exception {
    HttpGatewayRequest req = new HttpGatewayRequest("server ping") ;
    DumpResponseHandler handler = new DumpResponseHandler() ;
    AsyncHttpClient client = new AsyncHttpClient ("127.0.0.1", 8080, handler) ;
    client.post("/cluster/rest", req);
    Thread.sleep(1000);
    client.close();
    assertEquals(1, handler.getCount()) ;
  }
}