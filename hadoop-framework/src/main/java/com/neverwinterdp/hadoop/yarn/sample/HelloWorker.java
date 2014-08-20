package com.neverwinterdp.hadoop.yarn.sample;

import com.neverwinterdp.hadoop.yarn.app.worker.AppWorker;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainer;
import com.neverwinterdp.util.MapUtil;

public class HelloWorker implements AppWorker {
  public void run(AppWorkerContainer appContainer) throws Exception {
    long sleepTime = MapUtil.getLong(appContainer.getConfig().yarnConf, "hello-worker.sleep-time", 1000) ;
    for(int i = 0; i < 10; i++) {
      System.out.println("Hello Worker!") ;
      appContainer.reportProgress((i + 1)/(float)10);
      Thread.sleep(sleepTime);
    }
  }
}
