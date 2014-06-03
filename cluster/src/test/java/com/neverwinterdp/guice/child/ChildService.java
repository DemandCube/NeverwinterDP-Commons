package com.neverwinterdp.guice.child;

public class ChildService {
  
  public void hello() {
    System.out.println("Hello ChildService!");
  }
  
  public void finalize() {
    System.out.println("ChildService finalize() is called..............");
  }
}
