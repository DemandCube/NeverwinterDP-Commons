package com.neverwinterdp.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.neverwinterdp.util.text.TabularPrinter;

/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
@Singleton
public class RuntimeEnvironment {
  static int serverIdTracker ;
  
  private String serverName ;
  private String appDir;
  private String configDir;
  private String logDir;
  private String tmpDir;
  private String workingDir;
  private String dataDir;

  @Inject
  public RuntimeEnvironment(ServerConfig config) {
    serverName = config.getServerName();
    if(serverName == null) {
      synchronized(RuntimeEnvironment.class) {
        serverName  = "server" + ++serverIdTracker ;
      }
    }
    appDir = getSystemProperty("app.dir", "./") ;
    configDir = getSystemProperty("app.config.dir", appDir + "/config") ;
    logDir = getSystemProperty("app.log.dir", appDir + "/logs/" + serverName) ;
    tmpDir = getSystemProperty("app.tmp.dir", appDir + "/tmp/" + serverName) ;
    workingDir = getSystemProperty("app.working.dir", appDir + "/working/" + serverName) ;
    dataDir = getSystemProperty("app.data.dir", appDir + "/data/" + serverName) ;
  }
  
  public String getServerName() { return this.serverName ; }
  
  public String getAppDir() {
    return appDir;
  }

  public void setAppDir(String appDir) {
    this.appDir = appDir;
  }

  public String getConfigDir() {
    return configDir;
  }

  public void setConfigDir(String configDir) {
    this.configDir = configDir;
  }

  public String getLogDir() {
    return logDir;
  }

  public void setLogDir(String logDir) {
    this.logDir = logDir;
  }

  public String getTmpDir() {
    return tmpDir;
  }

  public void setTmpDir(String tmpDir) {
    this.tmpDir = tmpDir;
  }
  
  public String getWorkingDir() {
    return workingDir;
  }

  public void setWorkingDir(String workingDir) {
    this.workingDir = workingDir;
  }

  public String getDataDir() {
    return dataDir;
  }

  public void setDataDir(String dataDir) {
    this.dataDir = dataDir;
  }
  
  String getSystemProperty(String name, String defaultValue) {
    String value = System.getProperty(name) ;
    if(value != null) return value ;
    return defaultValue ;
  }
  
  public void dumpInfo(Appendable out) {
    int[] width = {35, 35} ;
    TabularPrinter p = new TabularPrinter(out, width) ;
    p.header("Runtime Environment", "");
    p.row("Server Name", serverName);
    p.row("App Dir", appDir);
    p.row("Config Dir", configDir) ;
    p.row("Log Dir", logDir);
    p.row("TMP Dir", tmpDir) ;
    p.row("Working Dir", workingDir);
    p.row("Data Dir", dataDir);
  }
}
