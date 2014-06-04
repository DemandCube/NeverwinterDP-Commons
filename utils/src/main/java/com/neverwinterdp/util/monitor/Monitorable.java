package com.neverwinterdp.util.monitor;

/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public interface Monitorable {
  public void init(MonitorRegistry mRegistry) ;
  public void reset(MonitorRegistry mRegistry) ;
}