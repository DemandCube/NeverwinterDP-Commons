package com.neverwinterdp.hadoop.yarn.app.ipc;

import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.neverwinterdp.hadoop.yarn.app.AppContainerInfoHolder;
import com.neverwinterdp.hadoop.yarn.app.AppInfo;
import com.neverwinterdp.hadoop.yarn.app.protocol.Ack;
import com.neverwinterdp.hadoop.yarn.app.protocol.Ack.Status;
import com.neverwinterdp.hadoop.yarn.app.protocol.AppContainerInfoList;
import com.neverwinterdp.hadoop.yarn.app.protocol.AppContainerReport;
import com.neverwinterdp.hadoop.yarn.app.protocol.AppContainerStatus;
import com.neverwinterdp.hadoop.yarn.app.protocol.AppContainerStatusList;
import com.neverwinterdp.hadoop.yarn.app.protocol.AppMasterInfo;
import com.neverwinterdp.hadoop.yarn.app.protocol.AppMasterStatus;
import com.neverwinterdp.hadoop.yarn.app.protocol.IPCService;
import com.neverwinterdp.hadoop.yarn.app.protocol.Void;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;

public class AppIPCService implements IPCService.BlockingInterface {
  private AppMaster appMaster ;
  
  public AppIPCService(AppMaster appMaster) {
    this.appMaster = appMaster ;
  }
  
  @Override
  public Ack updateAppContainerStatus(RpcController controller, AppContainerStatus status) {
    AppInfo appInfo = appMaster.getAppInfo() ;
    AppContainerInfoHolder containerInfoHolder = appInfo.getAppContainerInfoHolder(status.getContainerId()) ;
    if(containerInfoHolder == null) {
      return errAck("Cannot find the container " + status.getContainerId());
    } else {
      containerInfoHolder.setAppContainerStatus(status);
      return okAck();
    }
  }

  @Override
  public Ack updateAppContainerReport(RpcController controller, AppContainerReport report) throws ServiceException {
    AppInfo appInfo = appMaster.getAppInfo() ;
    AppContainerInfoHolder containerInfoHolder = appInfo.getAppContainerInfoHolder(report.getContainerId()) ;
    containerInfoHolder.mergeAppContainerReport(report);
    return okAck();
  }

  @Override
  public AppContainerStatusList getAppContainerStatusList(RpcController controller, Void request) throws ServiceException {
    AppInfo appInfo = appMaster.getAppInfo() ;
    return appInfo.getAppContainerStatusList();
  }

  @Override
  public AppContainerInfoList getAppContainerInfoList(RpcController controller, Void request) throws ServiceException {
    AppInfo appInfo = appMaster.getAppInfo() ;
    return appInfo.getAppContainerInfoList();
  }

  @Override
  public AppMasterStatus getAppMasterStatus(RpcController controller, Void request) throws ServiceException {
    AppInfo appInfo = appMaster.getAppInfo() ;
    return appInfo.getAppMasterInfoHolder().getAppMasterInfo().getStatus();
  }

  @Override
  public AppMasterInfo getAppMasterInfo(RpcController controller, Void request) throws ServiceException {
    AppInfo appInfo = appMaster.getAppInfo() ;
    return appInfo.getAppMasterInfoHolder().getAppMasterInfo();
  }
  
  public void close() { }
  
  private Ack okAck() {
    Ack.Builder ackB = Ack.newBuilder() ;
    ackB.setStatus(Status.OK) ;
    return ackB.build();
  }
  
  private Ack errAck(String message) {
    Ack.Builder ackB = Ack.newBuilder() ;
    ackB.setStatus(Status.ERROR) ;
    ackB.setMessage(message) ;
    return ackB.build();
  }
}