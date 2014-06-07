package com.neverwinterdp.server.command;

import com.neverwinterdp.server.cluster.ClusterMember;

/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class ServerCommandResult <T> {
  private T result ;
  private Exception error ;
  private ClusterMember fromMember ;
  
  public T getResult() {
    return result;
  }

  public void setResult(T result) {
    this.result = result;
  }
  
  public boolean hasError() { return error != null ; }
  
  public Exception getError() {
    return error;
  }
  
  public void setError(Exception error) {
    this.error = error;
  }

  public ClusterMember getFromMember() {
    return fromMember;
  }

  public void setFromMember(ClusterMember fromMember) {
    this.fromMember = fromMember;
  }
  
  public String toJSON() {
    return "this is a tets" ;
  }
}
