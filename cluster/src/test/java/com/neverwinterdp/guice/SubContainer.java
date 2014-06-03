package com.neverwinterdp.guice;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class SubContainer {
  @Inject 
  private Injector container ;
  
  public void dump() {
    System.out.println("injector container hash code = " + container.hashCode());
  }
}
