package com.neverwinterdp.hadoop.yarn.app.worker;

public interface AppWorker {
  public void run(AppWorkerContainer appContainer) throws Exception ;
}
