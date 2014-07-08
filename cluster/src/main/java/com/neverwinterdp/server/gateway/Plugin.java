package com.neverwinterdp.server.gateway;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.util.JSONSerializer;

abstract public class Plugin {
  protected ClusterClient clusterClient ;
  
  public void init(ClusterClient clusterClient) {
    this.clusterClient = clusterClient;
  }
  
  public <T> T execute(String command, String jsonParams) throws Exception {
    CommandParams params = JSONSerializer.INSTANCE.fromString(jsonParams, CommandParams.class) ;
    return (T) doCall(command, params) ;
  }
  
  public <T> T execute(String command, CommandParams params) throws Exception {
    return (T) doCall(command, params) ;
  }

  public String call(String json) {
    try {
      CommandParams params = JSONSerializer.INSTANCE.fromString(json, CommandParams.class) ;
      String commandName = params.getString("_commandName") ;
      return call(commandName, params) ;
    } catch(Throwable t) {
      Map<String, Object> result = new HashMap<String, Object>() ;
      result.put("success", false) ;
      result.put("message", t.getMessage()) ;
      return JSONSerializer.INSTANCE.toString(result) ;
    }
  }
  
  public String call(String command, CommandParams params) {
    try {
      Object result = doCall(command, params) ;
      if(result != null) return JSONSerializer.INSTANCE.toString(result) ;
      return "{ 'success': false, 'message': 'unknown command'}" ;
    } catch(Throwable t) {
      Map<String, Object> result = new HashMap<String, Object>() ;
      result.put("success", false) ;
      result.put("message", t.getMessage()) ;
      return JSONSerializer.INSTANCE.toString(result) ;
    }
  }
  
  abstract protected Object doCall(String command, CommandParams parans) throws Exception ;
  
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
