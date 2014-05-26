package com.neverwinterdp.cluster.guice;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceExample {
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new GuiceModule());
    Service1 service1 = injector.getInstance(Service1.class);
    service1.execute();
    System.out.println("--------------------------------------");
    Service2 service2 = injector.getInstance(Service2.class);
    service2.execute();
    
    System.out.println("--------------------------------------");
    System.out.println("injector hash code " + injector.hashCode());
    SubContainer subContainer = injector.getInstance(SubContainer.class);
    subContainer.dump();
  }
}