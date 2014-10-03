package com.neverwinterdp.server.yara;

import java.io.Serializable;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.neverwinterdp.server.RuntimeEnvironment;
import com.neverwinterdp.server.service.ServiceInfo;
import com.neverwinterdp.util.MapUtil;

public class YaraConfig extends ServiceInfo implements Serializable {
  private String serverName ;
  private String rpcHost ;
  private int rpcPort ;
  private String clientDataBufferDir ;
  
  @Inject
  public void init(RuntimeEnvironment rtEnv, @Named("yaraProperties") Map<String, String> props) {
    this.serverName = rtEnv.getServerName() ;
    rpcHost = MapUtil.getString(props, "rpc.host", "127.0.0.1") ;
    rpcPort = MapUtil.getInteger(props, "rpc.port", 8463) ;
    this.clientDataBufferDir = rtEnv.getDataDir() + "/yara/client" ; 
  }
  
  public String getServerName() { return this.serverName ; }
  
  public String getRpcHost() { return this.rpcHost ; }
  
  public int getRpcPort() { return this.rpcPort ; }

  public String getClientDataBufferDir() { return this.clientDataBufferDir ; }
}