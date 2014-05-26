package com.neverwinterdp.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * @author Tuan Nguyen
 * @email tuan08@gmail.com
 */
@Singleton
public class ServerConfig {
  @Inject(optional = true) @Named("server.group")
  private String   group;
  
  @Inject(optional = true) @Named("server.host")
  private String   host = "127.0.0.1";

  @Inject(optional = true) @Named("server.listen-port")
  private int      listenPort = 5700;

  @Inject(optional = true) @Named("server.version")
  private float    version = 1.0f;
  
  private String[] roles = {"worker"};
  
  @Inject(optional = true) @Named("server.cluster-framework")
  private String   clusterFramework ;
  
  @Inject(optional = true) @Named("server.description")
  private String   description = "a server instance";

  public String getGroup() { return group; }
  public void setGroup(String clusterName) {
    this.group = clusterName;
  }

  public String getHost() { return host;}
  public void setHost(String host) {
    this.host = host;
  }

  public float getVersion() { return version; }
  public void setVersion(float version) {
    this.version = version;
  }

  public int getListenPort() { return listenPort;}
  public void setListenPort(int listenPort) {
    this.listenPort = listenPort;
  }

  public String[] getRoles() { return roles; }
  
  @Inject(optional = true) 
  public void setRoles(@Named("server.cluster-framework") String roles) {
    this.roles = roles.split(",");
  }

  public String getClusterFramework() {
    return clusterFramework;
  }

  public void setClusterFramework(String clusterFramework) {
    this.clusterFramework = clusterFramework;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
