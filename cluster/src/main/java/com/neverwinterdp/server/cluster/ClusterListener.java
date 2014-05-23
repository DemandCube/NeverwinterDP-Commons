package com.neverwinterdp.server.cluster;

/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public interface ClusterListener<T> {
  public void onEvent(T listener, ClusterEvent event) ;
}
