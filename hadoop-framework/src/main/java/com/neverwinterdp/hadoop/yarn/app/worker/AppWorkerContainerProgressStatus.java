package com.neverwinterdp.hadoop.yarn.app.worker;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

public class AppWorkerContainerProgressStatus implements Serializable, Writable {
  
  private AppWorkerContainerState containerState = AppWorkerContainerState.ALLOCATED ;
  private float          progress = 0f;
  private String         statusMessage ;
  private Throwable      error ;
  
  public AppWorkerContainerProgressStatus() {
  }

  public AppWorkerContainerProgressStatus(AppWorkerContainerState containerState) {
    this.containerState = containerState ;
  }
  
  public float getProgress() {
    return progress;
  }

  public void setProgress(float progress) {
    this.progress = progress;
  }

  public String getStatusMessage() { return statusMessage; }

  public void setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
  }

  public AppWorkerContainerState getContainerState() {
    return containerState;
  }

  public void setContainerState(AppWorkerContainerState containerState) {
    this.containerState = containerState;
  }

  public Throwable getError() {
    return error;
  }

  public void setError(Throwable error) {
    this.error = error;
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeUTF(containerState.toString());
    out.writeFloat(progress);
    WritableUtils.writeString(out, statusMessage);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    containerState = AppWorkerContainerState.valueOf(in.readUTF()) ;
    progress = in.readFloat() ;
    statusMessage = WritableUtils.readString(in) ;
  }
}
