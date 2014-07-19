package com.neverwinterdp.server.gateway;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.util.JSONSerializer;

abstract public class CommandPlugin {
  protected ClusterClient clusterClient ;
  private Map<String, SubCommandExecutor> subCommandExecutors = new HashMap<String, SubCommandExecutor>() ;
  
  public void init(ClusterClient clusterClient) {
    this.clusterClient = clusterClient;
  }
  
  public void add(String subCommand, SubCommandExecutor executor) {
    subCommandExecutors.put(subCommand, executor) ;
  }
  
  public String call(Command command) {
    try {
      String subcommand = command.getSubCommand() ;
      SubCommandExecutor executor = this.subCommandExecutors.get(subcommand) ;
      if(executor != null) {
        Object result = executor.execute(clusterClient, command) ;
        return JSONSerializer.INSTANCE.toString(result) ;
      }
      return "{ 'success': false, 'message': 'unknown command'}" ;
    } catch(Throwable t) {
      Map<String, Object> result = new HashMap<String, Object>() ;
      result.put("success", false) ;
      result.put("message", t.getMessage()) ;
      return JSONSerializer.INSTANCE.toString(result) ;
    }
  }
  
  public String call(String commandLine) {
    return call(new Command(commandLine)) ;
  }
  
  public <T> T execute(Command command) throws Exception {
    String subcommand = command.getSubCommand() ;
    SubCommandExecutor executor = this.subCommandExecutors.get(subcommand) ;
    if(executor != null) {
      return (T) executor.execute(clusterClient, command) ;
    }
    throw new Exception("Unknown: " + command.getCommandLine()) ;
  }
  
  static public interface SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception ;
  }
  
  static public class Util {
    static public Map<String, CommandPlugin> loadByAnnotation(String ... packages) {
      Map<String, CommandPlugin> holder = new HashMap<String, CommandPlugin>() ;
      for(String pkg : packages) {
        Reflections reflections = new Reflections(pkg);
        Set<Class<?>> annotateds = reflections.getTypesAnnotatedWith(CommandPluginConfig.class);
        for(Class<?> clazz : annotateds) {
          try {
            CommandPlugin plugin  = (CommandPlugin) clazz.newInstance();
            CommandPluginConfig config = clazz.getAnnotation(CommandPluginConfig.class) ;
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