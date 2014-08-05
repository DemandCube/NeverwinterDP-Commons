package com.neverwinterdp.hadoop.yarn.app.worker;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.List;

import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.NodeId;

public class AppWorkerContainerInfo implements Serializable {
  private int    containerId ;
  private String nodeId ;
  private int    memory ;
  private int    cores ;
  private List<String> commands ;
  private AppWorkerContainerProgressStatus progressStatus ;
  private InetSocketAddress rpcAddress ;
  
  public AppWorkerContainerInfo() {}
      
  public AppWorkerContainerInfo(Container container, List<String> commands) {
    nodeId = container.getNodeId().toString() ;
    this.containerId = container.getId().getId() ;
    this.memory = container.getResource().getMemory() ;
    this.cores = container.getResource().getVirtualCores() ;
    this.commands = commands ;
    this.progressStatus = new AppWorkerContainerProgressStatus(AppWorkerContainerState.ALLOCATED) ;
  }

  public int getContainerId() { return containerId; }

  public void setContainerId(ContainerId containerId) {
    this.containerId = containerId.getId() ;
  }

  public String getNodeId() { return nodeId; }

  public void setNodeId(NodeId nodeId) { this.nodeId = nodeId.toString() ; }

  public int getMemory() {
    return memory;
  }

  public void setMemory(int memory) {
    this.memory = memory;
  }

  public int getCores() {
    return cores;
  }

  public void setCores(int cores) {
    this.cores = cores;
  }

  public List<String> getCommands() {
    return commands;
  }

  public void setCommands(List<String> commands) {
    this.commands = commands;
  }

  public AppWorkerContainerProgressStatus getProgressStatus() {
    return progressStatus;
  }

  public void setProgressStatus(AppWorkerContainerProgressStatus progressStatus) {
    this.progressStatus = progressStatus;
  }
  
  public InetSocketAddress getRpcAddress() { return this.rpcAddress ; }
  
  public void setRpcAddress(InetSocketAddress rpcAddress) {
    this.rpcAddress = rpcAddress ;
  }
}