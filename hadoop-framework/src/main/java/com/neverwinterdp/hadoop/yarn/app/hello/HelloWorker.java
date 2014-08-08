package com.neverwinterdp.hadoop.yarn.app.hello;

import com.neverwinterdp.hadoop.yarn.app.worker.AppWorker;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainer;

public class HelloWorker implements AppWorker {
  public void run(AppWorkerContainer appContainer) throws Exception {
    for(int i = 0; i < 10; i++) {
      System.out.println("Hello Worker!") ;
      appContainer.reportProgress((i + 1)/(float)10);
      Thread.sleep(5000);
    }
  }
}
