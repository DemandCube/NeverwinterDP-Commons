package com.neverwinterdp.yara;

import com.google.protobuf.InvalidProtocolBufferException;
import com.neverwinterdp.buffer.chronicle.Serializer;
import com.neverwinterdp.yara.protocol.LogRequest;

public class LogRequestSerializer implements Serializer<LogRequest> {

  public byte[] toBytes(LogRequest object) {
    byte[] data = object.toByteArray() ;
    return data ;
  }

  public LogRequest fromBytes(byte[] data) {
    try {
      return LogRequest.PARSER.parseFrom(data);
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException(e) ;
    }
  }
}
