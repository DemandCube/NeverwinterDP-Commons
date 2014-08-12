package com.neverwinterdp.hadoop.yarn.app.worker;

public interface AppWorkerContainerRPC {
  public String ping(String msg);

  public boolean kill();
}
