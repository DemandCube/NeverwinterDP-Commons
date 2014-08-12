package com.neverwinterdp.hadoop.yarn.app.worker.ipc;

public interface AppWorkerContainerRPC {
  public String ping(String msg);

  public boolean kill();
}
