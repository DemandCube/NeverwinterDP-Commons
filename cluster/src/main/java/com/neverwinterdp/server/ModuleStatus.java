package com.neverwinterdp.server;

import java.io.Serializable;

public class ModuleStatus implements Serializable {
  static public enum RunningStatus {INIT, START, STOP }
  
  static public enum InstallStatus {AVAILABLE, INSTALLED }
  
  private String moduleName;
  private String configureClass;
  private InstallStatus installStatus =  InstallStatus.AVAILABLE ;
  private RunningStatus runningStatus = RunningStatus.INIT ;

  public String getModuleName() {
    return moduleName;
  }

  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  public String getConfigureClass() {
    return configureClass;
  }

  public void setConfigureClass(String configureClass) {
    this.configureClass = configureClass;
  }

  public InstallStatus getInstallStatus() {
    return installStatus;
  }

  public void setInstallStatus(InstallStatus installStatus) {
    this.installStatus = installStatus;
  }

  public RunningStatus getRunningStatus() {
    return runningStatus;
  }

  public void setRunningStatus(RunningStatus runningStatus) {
    this.runningStatus = runningStatus;
  }
}
