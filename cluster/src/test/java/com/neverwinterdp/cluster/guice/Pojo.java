package com.neverwinterdp.cluster.guice;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Pojo {
  private String foo ;
  private boolean baz ;
  
  @Inject @Named("integer")
  private int integer;
  
  @Inject @Named("foo.baz")
  private String fooBaz ;

  private String[] array ;
  
  @Inject(optional = true) @Named("optional")
  private String optional = "optional";

  @Inject
  public Pojo(@Named("foo") String foo, @Named("baz") boolean baz) {
    this.foo = foo ;
    this.baz = baz ;
  }
  
  @Inject(optional = true)
  public void setArray(@Named("array") String arrays) {
    array = arrays.split(",") ;
  }
  
  @Override
  public String toString() {
    System.out.println("foo = " + foo);
    System.out.println("baz = " + baz);
    System.out.println("integer = " + integer);
    System.out.println("foo.bazz = " + fooBaz);
    System.out.println("array = " + array);
    System.out.println("optional = " + optional);
    return "Feitooooo!!";
  }
}
