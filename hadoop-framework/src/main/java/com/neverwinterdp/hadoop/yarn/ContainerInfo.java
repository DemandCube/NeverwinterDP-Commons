package com.neverwinterdp.hadoop.yarn;

import java.util.List;

import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.NodeId;

public class ContainerInfo {
  private String host ;
  private int    port ;
  private int    containerId ;
  private int    memory ;
  private int    cores ;
  private List<String> commands ;
  private String completeStatus ;
  
  public ContainerInfo() {}
      
  public ContainerInfo(Container container, List<String> commands) {
    NodeId nid = container.getNodeId() ;
    this.host = nid.getHost() ;
    this.port = nid.getPort() ;
    this.containerId = container.getId().getId() ;
    this.memory = container.getResource().getMemory() ;
    this.cores = container.getResource().getVirtualCores() ;
    this.commands = commands ;
  }

  public String getHost() { return host; }
  public void setHost(String host) { this.host = host; }

  public int getPort() { return port; }
  public void setPort(int port) { this.port = port; }

  public int getContainerId() { return containerId; }
  public void setContainerId(int containerId) { this.containerId = containerId; }

  public List<String> getCommands() { return commands; }
  public void setCommands(List<String> commands) { this.commands = commands; }

  public int getMemory() { return memory; }
  public void setMemory(int memory) { this.memory = memory; }

  public int getCores() { return cores; }
  public void setCores(int cores) { this.cores = cores; }

  public String getCompleteStatus() { return completeStatus; }
  public void setCompleteStatus(String completeStatus) {
    this.completeStatus = completeStatus;
  }
}