package com.neverwinterdp.hadoop.yarn.app.worker;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.yarn.api.records.Container;

public class AppWorkerContainerInfo implements Serializable {
  private int    containerId ;
  private String nodeId ;
  private int    memory ;
  private int    cores ;
  private List<String> commands ;
  private AppWorkerContainerProgressStatus progressStatus ;
  private Map<String, Serializable> reports  = new HashMap<String, Serializable> () ;
  
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
  
  public void setContainerId(int containerId) {
    this.containerId = containerId ;
  }
  
  public String getNodeId() { return nodeId; }
  public void setNodeId(String nodeId) { this.nodeId = nodeId ; }

  public int getMemory() { return memory; }
  public void setMemory(int memory) { this.memory = memory; }

  public int getCores() { return cores; }
  public void setCores(int cores) { this.cores = cores; }

  public List<String> getCommands() { return commands; }
  public void setCommands(List<String> commands) { this.commands = commands; }

  public void setReport(String name, Serializable report) {
    reports.put(name, report) ;
  }
  
  /**json serializer may need to have get/set method so I create get/set. should use add report method*/
  public Map<String, Object> getReports() {
    return reports;
  }

  public void setReports(Map<String, Object> reports) {
    this.reports = reports;
  }

  public AppWorkerContainerProgressStatus getProgressStatus() { return progressStatus; }
  public void setProgressStatus(AppWorkerContainerProgressStatus progressStatus) {
    this.progressStatus = progressStatus;
  }
  
  
}