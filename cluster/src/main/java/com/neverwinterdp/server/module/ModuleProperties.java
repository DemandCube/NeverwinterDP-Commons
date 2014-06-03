package com.neverwinterdp.server.module;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ModuleProperties {
  @Inject @Named("module.data.drop")
  private boolean dataDrop ;
  
  @Inject @Named("module.data.dir")
  private String  dataDir ;

  public boolean isDataDrop() {
    return dataDrop;
  }

  public void setDataDrop(boolean dataDrop) {
    this.dataDrop = dataDrop;
  }

  public String getDataDir() {
    return dataDir;
  }

  public void setDataDir(String dataDir) {
    this.dataDir = dataDir;
  }
}
