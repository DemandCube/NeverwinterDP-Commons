package com.neverwinterdp.hadoop.yarn.app.http.netty.webapp;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neverwinterdp.hadoop.yarn.app.AppConfig;
import com.neverwinterdp.hadoop.yarn.app.http.netty.NettyHttpService;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.netty.http.client.DumpResponseHandler;
import com.neverwinterdp.netty.http.client.AsyncHttpClient;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class WebAppUnitTest {
  private NettyHttpService httpService ;
  private int port = 8181;
  @Before
  public void setup() throws Exception {
    AppMaster appMaster = new AppMaster() ;
    appMaster.mock(new AppConfig()) ;
    httpService = new NettyHttpService(appMaster,port);
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
    AsyncHttpClient client = new AsyncHttpClient ("127.0.0.1", port, handler) ;
    client.get("/info");
    
    Thread.sleep(1000);
    assertEquals(1, handler.getCount()) ;
  }
}