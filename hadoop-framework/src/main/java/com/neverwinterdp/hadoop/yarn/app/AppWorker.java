package com.neverwinterdp.hadoop.yarn.app;

public interface AppWorker {
  public void run(AppContainer appContainer) throws Exception ;
}
