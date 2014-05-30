package com.neverwinterdp.guice;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.neverwinterdp.guice.child.ChildModule;
import com.neverwinterdp.guice.child.ChildService;

public class GuiceExample {
  public static void main(String[] args) throws Exception {
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
    
    Injector childInjector = injector.createChildInjector(new ChildModule()) ;
    ChildService childService = childInjector.getInstance(ChildService.class) ;
    childService.hello();
    System.out.println("Start destroy child injector...............") ;
    childService = null;
    childInjector = null ;
    System.gc();
    Thread.sleep(1000);
    System.out.println("Finish destroy child injector...............") ;
    
  }
}