package com.neverwinterdp.hadoop.yarn.app.http.netty.webapp;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neverwinterdp.hadoop.yarn.app.AppConfig;
import com.neverwinterdp.hadoop.yarn.app.http.netty.NettyHttpService;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.netty.http.client.DumpResponseHandler;
import com.neverwinterdp.netty.http.client.HttpClient;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class WebAppUnitTest {
  private NettyHttpService httpService ;
  
  @Before
  public void setup() throws Exception {
    AppMaster appMaster = new AppMaster() ;
    appMaster.mock(new AppConfig()) ;
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
    DumpResponseHandler handler = new DumpResponseHandler() ;
    HttpClient client = new HttpClient ("127.0.0.1", 8080, handler) ;
    client.get("/info");
    
    Thread.sleep(1000);
    assertEquals(1, handler.getCount()) ;
  }
}