package com.neverwinterdp.hadoop.yarn.app.ipc;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

import com.neverwinterdp.util.JSONSerializer;

public class ReportData implements Serializable, Writable {
  private String name ;
  private String type ;
  private String jsonData ;
  
  public ReportData() { }
  
  public ReportData(String name, Object data) {
    this.name = name ;
    this.type = data.getClass().getName() ;
    this.jsonData = JSONSerializer.INSTANCE.toString(data) ;
  }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getType() { return type; }
  public void setType(String type) { this.type = type; }

  public String getJsonData() { return jsonData; }
  public void setJsonData(String jsonData) { this.jsonData = jsonData; }
  
  public <T>  T getDataAs(Class<T> type) {
    return JSONSerializer.INSTANCE.fromString(jsonData, type) ;
  }
  
  @Override
  public void write(DataOutput out) throws IOException {
    WritableUtils.writeString(out, name);
    WritableUtils.writeString(out, type);
    WritableUtils.writeString(out, jsonData);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    name = WritableUtils.readString(in) ;
    type = WritableUtils.readString(in) ;
    jsonData = WritableUtils.readString(in) ;
  }
}
