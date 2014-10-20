package com.neverwinterdp.hadoop.yarn.app.ipc;


import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.hadoop.yarn.app.ipc.AppIPCService;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.hadoop.yarn.app.protocol.Ack;
import com.neverwinterdp.hadoop.yarn.app.protocol.AppContainerStatus;
import com.neverwinterdp.hadoop.yarn.app.protocol.IPCService;
import com.neverwinterdp.netty.rpc.client.DefaultClientRPCController;
import com.neverwinterdp.netty.rpc.client.RPCClient;
import com.neverwinterdp.netty.rpc.server.RPCServer;

public class AppIPCServiceUnitTest {
  static protected RPCServer server  ;
  static protected RPCClient client ;
  
  @BeforeClass
  static public void setup() throws Exception {
    server = new RPCServer() ;
    server.startAsDeamon(); 
    
    client = new RPCClient() ;
  }
  
  @AfterClass
  static public void teardown() {
    client.close();
    server.shutdown();
  }
  
  @Test
  public void testIPCService() throws Exception {
    server.getServiceRegistry().register(IPCService.newReflectiveBlockingService(new AppIPCService(new AppMaster())));
    IPCService.BlockingInterface blockingService = IPCService.newBlockingStub(client.getRPCChannel()) ;
    AppContainerStatus.Builder statusB = AppContainerStatus.newBuilder() ;
    statusB.setContainerId(1) ;
    statusB.setStartTime(System.currentTimeMillis()) ;
    statusB.setFinishTime(System.currentTimeMillis()) ;
    statusB.setProgress(0) ;
    statusB.setStatusMessage("test") ;
    Ack ack = blockingService.updateAppContainerStatus(new DefaultClientRPCController(), statusB.build());
    Assert.assertEquals(ack.getStatus(), Ack.Status.ERROR);
  }
}