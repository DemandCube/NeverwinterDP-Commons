package com.neverwinterdp.hadoop.yarn.app.hello.webapp;

import org.apache.hadoop.classification.InterfaceAudience;

/**
 * Context interface for sharing information across components in YARN App.
 */
@InterfaceAudience.Private
public interface AppContext {
  String getApplicationId(); 
  
  String getApplicationName();

  String getUser();
}