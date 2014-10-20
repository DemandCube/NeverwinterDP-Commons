package com.neverwinterdp.hadoop.yarn.app.http.netty.webapp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neverwinterdp.hadoop.yarn.app.AppConfig;
import com.neverwinterdp.hadoop.yarn.app.AppInfo;
import com.neverwinterdp.hadoop.yarn.app.http.netty.NettyHttpService;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class WebAppIntegrationTest {
  private NettyHttpService httpService ;
  
  @Before
  public void setup() throws Exception {
    AppConfig appConfig = new AppConfig() ;
    appConfig.appHome = "Mock/App/Home" ;
    AppMaster appMaster = new AppMaster() ;
    appMaster.mock(appConfig) ;
    AppInfo appInfo = new AppInfo() ;
    appMaster.mock(appInfo) ;
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