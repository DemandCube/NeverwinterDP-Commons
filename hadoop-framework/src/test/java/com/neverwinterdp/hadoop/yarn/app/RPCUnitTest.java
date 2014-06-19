package com.neverwinterdp.hadoop.yarn.app;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.ProtocolInfo;
import org.apache.hadoop.ipc.RPC;
import org.junit.Test;

public class RPCUnitTest {
  @ProtocolInfo(protocolName = "ping", protocolVersion = 1)
  static interface PingRPC {
      String ping(String msg);
  }
 
  static class PingRPCImpl implements PingRPC {
    public String ping(String msg) { return "pong=" + msg; }
  }
  
  @Test
  public void test() throws Exception {
    final RPC.Server server = 
        new RPC.Builder(new Configuration()).
        setInstance(new PingRPCImpl()).
        setProtocol(PingRPC.class).
        build();
    server.start();
    
    PingRPC ping = 
        RPC.getProxy(PingRPC.class, RPC.getProtocolVersion(PingRPC.class), server.getListenerAddress(), new Configuration());
    System.out.println("Server ping returned " + ping.ping("X"));
    server.stop();
  }
}
