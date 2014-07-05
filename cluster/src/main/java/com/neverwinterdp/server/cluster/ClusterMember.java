package com.neverwinterdp.server.cluster;


/**
 * @author Tuan Nguyen
 * @email tuan08@gmail.com
 */
public interface ClusterMember {
  public String getUuid();

  public String getHost();

  public String getIpAddress();

  public int getPort();
  
  public String getMemberName() ;
}
