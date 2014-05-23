package com.neverwinterdp.server.cluster;

import java.util.Set;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public interface ClusterMember {
  public String getUuid() ;
  public String getHost() ;
  public String getIpAddress() ;
  public int getPort() ;
  public float getVersion() ;
  public Set<String> getRoles() ;
  public String getDescription() ;
}
