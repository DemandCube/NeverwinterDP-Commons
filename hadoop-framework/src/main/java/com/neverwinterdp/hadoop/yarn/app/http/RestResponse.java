package com.neverwinterdp.hadoop.yarn.app.http;

public class RestResponse {
  private RestRequest request ;
  private Object      data ;
  
  public RestResponse() {
  }
  
  public RestResponse(RestRequest request) {
    this.request = request ;
  }

  public RestRequest getRequest() { return request; }
  public void setRequest(RestRequest request) { this.request = request; }

  public Object getData() { return data; }

  public void setData(Object result) { this.data = result; }
}
