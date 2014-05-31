package com.neverwinterdp.server.module;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class ModuleUnitTest {
  @Test
  public void testLoadModuleRegistration() {
    Map<String, ModuleRegistration> holder = new HashMap<String, ModuleRegistration>() ;
    ModuleRegistration.loadByAnnotation(holder, "com.neverwinterdp.server.module");
    ModuleRegistration hello = holder.get("HelloModule");
    assertNotNull(hello) ;
    assertEquals("HelloModule", hello.getModuleName()) ;
    assertEquals(HelloModule.class.getName(), hello.getConfigureClass()) ;
    assertTrue(hello.isAutoInstall()) ;
    assertTrue(hello.isAutostart()) ;
    
    ModuleRegistration helloDisable = holder.get("HelloModuleDisable");
    assertNotNull(helloDisable) ;
    assertEquals("HelloModuleDisable", helloDisable.getModuleName()) ;
    assertEquals(HelloModuleDisable.class.getName(), helloDisable.getConfigureClass()) ;
    assertFalse(helloDisable.isAutoInstall()) ;
    assertFalse(helloDisable.isAutostart()) ;
  }
}
