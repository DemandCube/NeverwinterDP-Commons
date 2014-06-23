package com.neverwinterdp.server.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.neverwinterdp.server.cluster.ClusterClient;

abstract public class Plugin {
  protected ClusterClient clusterClient ;
  
  public void init(ClusterClient clusterClient) {
    this.clusterClient = clusterClient;
  }
  
  abstract public String call(String params) ;
  
  static public class Util {
    static public Map<String, Plugin> loadByAnnotation(String ... packages) {
      Map<String, Plugin> holder = new HashMap<String, Plugin>() ;
      for(String pkg : packages) {
        Reflections reflections = new Reflections(pkg);
        Set<Class<?>> annotateds = reflections.getTypesAnnotatedWith(PluginConfig.class);
        for(Class<?> clazz : annotateds) {
          try {
            Plugin plugin  = (Plugin) clazz.newInstance();
            PluginConfig config = clazz.getAnnotation(PluginConfig.class) ;
            holder.put(config.name(), plugin) ;
          } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
          }
        }
      }
      return holder ;
    }
  }
}
