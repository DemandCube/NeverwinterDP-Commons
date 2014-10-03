package com.neverwinterdp.yara.client;

import com.google.protobuf.RpcController;
import com.neverwinterdp.netty.rpc.client.RPCClient;
import com.neverwinterdp.netty.rpc.client.DefaultClientRPCController;
import com.neverwinterdp.yara.protocol.LogRequest;
import com.neverwinterdp.yara.protocol.YaraService;

public class RPCLogRequestForwarder implements LogRequestForwarder {
  private RPCClient client ;
  private YaraService.BlockingInterface service ;
  private int count = 0;
  
  public RPCLogRequestForwarder(String host, int port) throws Exception {
    this(new RPCClient(host, port)) ;
  }
  
  public RPCLogRequestForwarder(RPCClient client) {
    this.client = client ;
    this.service = YaraService.newBlockingStub(client.getRPCChannel()) ;
  }
  
  public int getForwardCount() { return this.count ; }
  
  public void forward(LogRequest rlog) throws Exception {
    service.log(new DefaultClientRPCController(), rlog) ;
    count++ ;
  }
  
  public void close() { 
    client.close(); 
  }
}
