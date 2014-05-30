package com.neverwinterdp.guice;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Service1 {

	private final Pojo pojo;

	@Inject
	public Service1(Pojo pojo) {
	  System.out.println("service 1 hash code = " + hashCode()) ;
		this.pojo = pojo;
		System.out.println("Inject pojo, hash code = " + pojo.hashCode()) ;
	}

	public void execute() {
		System.out.println(pojo.toString());
	}
}
