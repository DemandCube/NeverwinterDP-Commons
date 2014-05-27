package com.neverwinterdp.cluster.guice;

import com.google.inject.Inject;

public class Service2 {
  @Inject
  private Service1 service1 ;
  
  @Inject
	private Pojo pojo;
	
	public void execute() {
	  System.out.println("service 1 hash code = " + service1.hashCode()) ;
	  System.out.println("Inject pojo, hash code = " + pojo.hashCode()) ;
		System.out.println(pojo.toString());
	}
}
