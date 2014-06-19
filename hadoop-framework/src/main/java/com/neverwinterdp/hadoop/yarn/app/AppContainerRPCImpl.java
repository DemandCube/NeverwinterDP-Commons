package com.neverwinterdp.hadoop.yarn.app;

public class AppContainerRPCImpl implements AppContainerRPC {

  public String ping(String msg) { return msg; }

  public boolean kill() {
    return false;
  }
}
