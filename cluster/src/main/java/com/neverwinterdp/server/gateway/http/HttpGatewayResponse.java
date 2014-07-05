package com.neverwinterdp.server.gateway.http;

public class HttpGatewayResponse {
  private HttpGatewayRequest request ;
  private Object             data ;
  
  public HttpGatewayResponse() {
  }
  
  public HttpGatewayResponse(HttpGatewayRequest request) {
    this.request = request ;
  }

  public HttpGatewayRequest getRequest() { return request; }
  public void setRequest(HttpGatewayRequest request) { this.request = request; }

  public Object getData() { return data; }

  public void setData(Object result) { this.data = result; }
}
