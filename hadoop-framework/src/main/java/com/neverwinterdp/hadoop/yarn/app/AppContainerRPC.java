package com.neverwinterdp.hadoop.yarn.app;

public interface AppContainerRPC {
  public String ping(String msg);

  public boolean kill();
}
