package com.neverwinterdp.hadoop.yarn.app.ipc;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

import com.neverwinterdp.util.JSONSerializer;

public class Response implements Serializable, Writable {
  private String type ;
  private String jsonData ;
  
  public Response() { }
  
  public Response(Object data) {
    this.type = data.getClass().getName() ;
    this.jsonData = JSONSerializer.INSTANCE.toString(data) ;
  }

  public String getType() { return type; }
  public void setType(String type) { this.type = type; }

  public String getJsonData() { return jsonData; }
  public void setJsonData(String jsonData) { this.jsonData = jsonData; }
  
  public <T>  T getDataAs(Class<T> type) {
    return JSONSerializer.INSTANCE.fromString(jsonData, type) ;
  }
  
  @Override
  public void write(DataOutput out) throws IOException {
    WritableUtils.writeString(out, type);
    WritableUtils.writeString(out, jsonData);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    type = WritableUtils.readString(in) ;
    jsonData = WritableUtils.readString(in) ;
  }
}