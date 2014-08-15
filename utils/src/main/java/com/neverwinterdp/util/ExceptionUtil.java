package com.neverwinterdp.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {
  static public String getStackTrace(Throwable t) {
    StringWriter swriter = new StringWriter() ;
    PrintWriter pwriter = new PrintWriter(swriter) ;
    t.printStackTrace(pwriter);
    pwriter.close(); 
    return swriter.toString() ;
  }
}
