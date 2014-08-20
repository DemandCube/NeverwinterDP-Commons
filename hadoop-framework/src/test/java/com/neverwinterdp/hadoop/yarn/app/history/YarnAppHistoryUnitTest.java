package com.neverwinterdp.hadoop.yarn.app.history;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.hadoop.yarn.app.AppInfo;
import com.neverwinterdp.hadoop.yarn.app.master.AppMasterMonitor;
import com.neverwinterdp.netty.http.HttpServer;
import com.neverwinterdp.netty.http.client.AsyncHttpClient;
import com.neverwinterdp.netty.http.client.DumpResponseHandler;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.gateway.ClusterGateway;
import com.neverwinterdp.util.IOUtil;
import com.neverwinterdp.util.JSONSerializer;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class YarnAppHistoryUnitTest {
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
        " -Phttp:route.names=yarn-app-history" +
        " -Phttp:route.yarn-app-history.handler=com.neverwinterdp.hadoop.yarn.app.history.AppHistoryRouteHandler" +
        " -Phttp:route.yarn-app-history.path=/yarn-app/history.*" +
        " --member-name webserver --autostart --module Http"
    ) ;
  }

  @AfterClass
  static public void teardown() throws Exception {
    gateway.close(); 
    instance.exit(0) ;
  }
  
  @Test
  public void testYarnAppHistory() throws Exception {
    AppHistorySender sender = new AppHistorySender("http://127.0.0.1:8080/yarn-app/history") ;
    
    DumpResponseHandler handler = new DumpResponseHandler() ;
    AsyncHttpClient client = new AsyncHttpClient ("127.0.0.1", 8080, handler) ;
    
    AppInfo appConfig = new AppInfo() ;
    appConfig.appHome = "Mock/App/Home" ;
    appConfig.appId   = "YarnApp" ;
    String appMonitorJson = IOUtil.getFileContentAsString("src/test/resources/AppMonitor.json") ;
    AppMasterMonitor appMonitor = JSONSerializer.INSTANCE.fromString(appMonitorJson, AppMasterMonitor.class) ;
    AppHistory appHistory = new AppHistory() ;
    appHistory.setAppInfo(appConfig);
    appHistory.setAppMonitor(appMonitor);
    sender.send(appHistory);
    
    Thread.sleep(500);
    client.get("/yarn-app/history/list");
    Thread.sleep(500);
    client.get("/yarn-app/history/app/YarnApp");
    Thread.sleep(500);
    sender.shutdown();
    client.close();
    assertEquals(2, handler.getCount()) ;
  }
}