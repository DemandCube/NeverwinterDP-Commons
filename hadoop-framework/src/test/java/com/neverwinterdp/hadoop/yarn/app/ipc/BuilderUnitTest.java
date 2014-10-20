package com.neverwinterdp.hadoop.yarn.app.ipc;

import org.junit.Test;

import com.neverwinterdp.hadoop.yarn.app.protocol.AppMasterInfo;
import com.neverwinterdp.hadoop.yarn.app.protocol.ProcessStatus;

public class BuilderUnitTest {
  @Test
  public void testBuilder() {
    AppMasterInfo.Builder minfoB = AppMasterInfo.newBuilder() ;
    minfoB.getStatusBuilder().setStartTime(System.currentTimeMillis()) ;
    minfoB.getStatusBuilder().setFinishTime(System.currentTimeMillis()) ;
    minfoB.getStatusBuilder().setProcessStatus(ProcessStatus.RUNNING) ;
    System.out.println(minfoB);
  }
}
