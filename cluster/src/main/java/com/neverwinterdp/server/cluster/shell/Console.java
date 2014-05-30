package com.neverwinterdp.server.cluster.shell;

import java.io.PrintStream;

import com.neverwinterdp.util.text.TabularPrinter;

public class Console {
  private PrintStream out ;
  private ConsoleAppendable consoleOutput ;
  
  public Console(PrintStream out) {
    this.out = out ;
  }
  
  public void newConsoleOutput() {
    consoleOutput = new ConsoleAppendable(out) ;
  }
  
  public String getTextOutput() { return consoleOutput.getTextOutput() ; }
  
  public TabularPrinter tabularPrinter(int ... colWidth) {
    return new TabularPrinter(consoleOutput, colWidth) ;
  }
  
  public void banner(String text) {
    text = "*" + center(text, 68) + "*" ;
    println('*', text.length());
    println(text) ;
    println('*', text.length());
  }

  public void header(String title) {
    println() ; 
    println(title) ;
    println('=', title.length());
    println() ;
  }

  public void println(String text) {
    consoleOutput.append(text);
    consoleOutput.append('\n') ;
  }
  
  public void println(Object ... text) {
    for(Object sel : text) {
      consoleOutput.append(sel.toString());
    }
    consoleOutput.append('\n') ;
  }
  
  public void println(String ... text) {
    for(Object sel : text) {
      consoleOutput.append(sel.toString());
    }
    consoleOutput.append('\n') ;
  }
  
  public void println() {
    consoleOutput.append('\n') ;
  }
  
  public void println(String text, int width) {
    consoleOutput.append(text) ;
    for(int i = text.length(); i < width; i++) {
      consoleOutput.append(' ') ;
    }
    consoleOutput.append('\n') ;
  }
  
  private void println(char c, int repeat) {
    for(int i = 0; i < repeat; i++) {
      consoleOutput.append(c) ;
    }
    consoleOutput.append('\n') ;
  }
  
  private String center(String text, int width) {
    if(text.length() >= width) return text ;
    StringBuilder b = new StringBuilder() ;
    int patch = (width - text.length()) / 2 ;
    for(int i = 0; i < patch; i++) {
      b.append(' ') ;
    }
    b.append(text) ;
    for(int i = 0; i < patch; i++) {
      b.append(' ') ;
    }
    return b.toString() ;
  }
}