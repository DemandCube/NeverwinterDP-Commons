package com.neverwinterdp.hadoop.yarn.app;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;

import com.neverwinterdp.hadoop.yarn.app.protocol.AppContainerInfo;
import com.neverwinterdp.hadoop.yarn.app.protocol.AppContainerInfoList;
import com.neverwinterdp.hadoop.yarn.app.protocol.AppContainerReport;
import com.neverwinterdp.hadoop.yarn.app.protocol.AppContainerStatus;
import com.neverwinterdp.hadoop.yarn.app.protocol.AppContainerStatusList;
import com.neverwinterdp.hadoop.yarn.app.protocol.ProcessStatus;

public class AppInfo  {
  private AtomicInteger completedContainerCount = new AtomicInteger() ;
  private AtomicInteger allocatedContainerCount = new AtomicInteger() ;
  private AtomicInteger failedContainerCount = new AtomicInteger() ;
  private AtomicInteger requestedContainerCount = new AtomicInteger() ;

  private AppMasterInfoHolder masterInfo = new AppMasterInfoHolder() ;
  private Map<Integer, AppContainerInfoHolder > containerInfos = new LinkedHashMap<Integer, AppContainerInfoHolder>() ;
  

  public AtomicInteger getCompletedContainerCount() {
    return completedContainerCount;
  }

  public AtomicInteger getAllocatedContainerCount() {
    return allocatedContainerCount;
  }

  public AtomicInteger getFailedContainerCount() {
    return failedContainerCount;
  }

  public AtomicInteger getRequestedContainerCount() {
    return requestedContainerCount;
  }
  
  public AppMasterInfoHolder getAppMasterInfoHolder() { return this.masterInfo ; }

  public AppContainerInfoHolder getAppContainerInfoHolder(int containerId) {
    return containerInfos.get(containerId) ;
  }
  
  public AppContainerInfoHolder[] getAppContainerInfoHolders() {
    AppContainerInfoHolder[] array = new AppContainerInfoHolder[containerInfos.size()] ;
    return containerInfos.values().toArray(array) ;
  }
  
  public AppContainerInfo[] getAppContainerInfos() {
    AppContainerInfoHolder[] holder = getAppContainerInfoHolders() ;
    AppContainerInfo[] info = new AppContainerInfo[holder.length];
    for(int i = 0; i < holder.length; i++) {
      info[i] = holder[i].getAppContainerInfo() ;
    }
    return info ;
  }
  
  public AppContainerStatusList getAppContainerStatusList() {
    AppContainerStatusList.Builder builder = AppContainerStatusList.newBuilder() ;
    for(AppContainerInfo sel : getAppContainerInfos()) builder.addContainerStatus(sel.getStatus()) ;
    return builder.build();
  }
  
  public AppContainerInfoList getAppContainerInfoList() {
    AppContainerInfoList.Builder builder = AppContainerInfoList.newBuilder() ;
    for(AppContainerInfo sel : getAppContainerInfos()) builder.addContainerInfo(sel) ;
    return builder.build();
  }
  
  
  public void onContainerRequest(ContainerRequest containerReq) {
    requestedContainerCount.incrementAndGet() ;
  }
  
  public void onCompletedContainer(ContainerStatus status) {
    AppContainerInfoHolder containerInfo = containerInfos.get(status.getContainerId().getId()) ;
    containerInfo.setProcessStatus(ProcessStatus.TERMINATED) ;
    completedContainerCount.incrementAndGet();
  }
  
  public void onFailedContainer(ContainerStatus status) {
    AppContainerInfoHolder containerInfo = containerInfos.get(status.getContainerId()) ;
    containerInfo.setProcessStatus(ProcessStatus.TERMINATED) ;
    failedContainerCount.incrementAndGet();
  }
  
  public void onAllocatedContainer(Container container, List<String> commands) {
    allocatedContainerCount.incrementAndGet() ;
    int containerId = container.getId().getId() ;
    AppContainerInfoHolder containerInfo = new AppContainerInfoHolder(containerId) ;
    containerInfos.put(containerId, containerInfo) ;
  }
  
  public void onUpdateAppContainerStatus(AppContainerStatus status) {
    AppContainerInfoHolder containerInfo = containerInfos.get(status.getContainerId()) ;
    containerInfo.setAppContainerStatus(status) ;
  }
  
  public void onUpdateAppContainerReport(AppContainerReport report) {
    AppContainerInfoHolder containerInfo = containerInfos.get(report.getContainerId()) ;
    containerInfo.mergeAppContainerReport(report);
  }
}