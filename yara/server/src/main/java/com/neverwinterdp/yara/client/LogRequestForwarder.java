package com.neverwinterdp.yara.client;

import com.neverwinterdp.yara.protocol.LogRequest;

public interface LogRequestForwarder {
  public void forward(LogRequest rlog) throws Exception ;
  public void close() ;
}
