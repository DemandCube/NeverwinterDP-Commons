package com.neverwinterdp.hadoop.yarn.app.http.netty.webapp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neverwinterdp.hadoop.yarn.app.AppInfo;
import com.neverwinterdp.hadoop.yarn.app.http.netty.NettyHttpService;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.hadoop.yarn.app.master.AppMasterMonitor;
import com.neverwinterdp.util.IOUtil;
import com.neverwinterdp.util.JSONSerializer;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class WebAppIntegrationTest {
  private NettyHttpService httpService ;
  
  @Before
  public void setup() throws Exception {
    AppInfo appConfig = new AppInfo() ;
    appConfig.appHome = "Mock/App/Home" ;
    AppMaster appMaster = new AppMaster() ;
    appMaster.mock(appConfig) ;
    String appMonitorJson = IOUtil.getFileContentAsString("src/test/resources/AppMonitor.json") ;
    appMaster.mock(JSONSerializer.INSTANCE.fromString(appMonitorJson, AppMasterMonitor.class)) ;
    httpService = new NettyHttpService(appMaster);
    httpService.start() ;
    Thread.sleep(1000);
  }
  
  @After
  public void teardown() {
    httpService.shutdown();
  }
  
  @Test
  public void testInfo() throws Exception {
    Thread.sleep(100000000);
  }
}