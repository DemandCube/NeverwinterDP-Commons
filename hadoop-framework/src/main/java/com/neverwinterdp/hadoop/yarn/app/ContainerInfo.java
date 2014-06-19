package com.neverwinterdp.hadoop.yarn.app;

import java.net.InetSocketAddress;
import java.util.List;

import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.NodeId;

public class ContainerInfo {
  private ContainerId  containerId ;
  private NodeId nodeId ;
  private int    memory ;
  private int    cores ;
  private List<String> commands ;
  private ContainerProgressStatus progressStatus ;
  private InetSocketAddress rpcAddress ;
  
  public ContainerInfo() {}
      
  public ContainerInfo(Container container, List<String> commands) {
    nodeId = container.getNodeId() ;
    this.containerId = container.getId() ;
    this.memory = container.getResource().getMemory() ;
    this.cores = container.getResource().getVirtualCores() ;
    this.commands = commands ;
    this.progressStatus = new ContainerProgressStatus(ContainerState.ALLOCATED) ;
  }

  public ContainerId getContainerId() {
    return containerId;
  }

  public void setContainerId(ContainerId containerId) {
    this.containerId = containerId;
  }

  public NodeId getNodeId() {
    return nodeId;
  }

  public void setNodeId(NodeId nodeId) {
    this.nodeId = nodeId;
  }

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

  public ContainerProgressStatus getProgressStatus() {
    return progressStatus;
  }

  public void setProgressStatus(ContainerProgressStatus progressStatus) {
    this.progressStatus = progressStatus;
  }
  
  public InetSocketAddress getRpcAddress() { return this.rpcAddress ; }
  
  public void setRpcAddress(InetSocketAddress rpcAddress) {
    this.rpcAddress = rpcAddress ;
  }
}