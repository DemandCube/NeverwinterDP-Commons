package com.neverwinterdp.hadoop.yarn.app.http;

public interface HttpService {
  public String getTrackingUrl() ;
  public void start() throws Exception ;
  public void shutdown() ;
}
