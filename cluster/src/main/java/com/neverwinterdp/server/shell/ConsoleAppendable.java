package com.neverwinterdp.server.shell;

import java.io.PrintStream;

public class ConsoleAppendable implements Appendable {
  private StringBuilder textOutput ;
  private PrintStream[] out ;
  
  public ConsoleAppendable(PrintStream ... out) {
    this.out = out ;
    this.textOutput = new StringBuilder() ;
  }
  
  public String getTextOutput() { 
    return textOutput.toString() ;
  }
  
  public Appendable append(CharSequence csq) {
    textOutput.append(csq) ;
    for(PrintStream sel : out) {
      sel.append(csq) ;
    }
    return this ;
  }

  public Appendable append(char c)  {
    textOutput.append(c) ;
    for(PrintStream sel : out) {
      sel.append(c) ;
    }
    return this ;
  }

  public Appendable append(CharSequence csq, int start, int end) {
    textOutput.append(csq, start, end) ;
    for(PrintStream sel : out) {
      sel.append(csq, start, end) ;
    }
    return this ;
  }
}