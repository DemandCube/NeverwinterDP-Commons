package com.neverwinterdp.hadoop.yarn.app.worker.ipc;

public class AppWorkerContainerRPCImpl implements AppWorkerContainerRPC {

  public String ping(String msg) { return msg; }

  public boolean kill() {
    return false;
  }
}
