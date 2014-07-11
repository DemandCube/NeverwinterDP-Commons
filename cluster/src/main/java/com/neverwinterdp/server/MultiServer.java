package com.neverwinterdp.server;

import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;

public class MultiServer {
  static public class MultiServerOptions {
    @DynamicParameter(names = "-P", description = "Module properties")
    Map<String, String> properties = new HashMap<String, String>();
  }
  
  static public class MultiProperties extends HashMap<String, Map<String, String>> {
    public void add(String key, String value) {
      int idx = key.indexOf(':') ;
      String name = key.substring(0, idx) ;
      String subkey = key.substring(idx + 1) ;
      Map<String, String> map = get(name) ;
      if(map == null) {
        map = new HashMap<String, String>() ;
        put(name, map) ;
      }
      map.put(subkey, value) ;
    }
  }
  
  static public Server[] create(String[] args) throws Exception {
    MultiServerOptions options = new MultiServerOptions() ;
    new JCommander(options, args) ;
    MultiProperties multiProperties = new MultiProperties() ;
    for(Map.Entry<String, String> entry : options.properties.entrySet()) {
      multiProperties.add(entry.getKey(), entry.getValue());
    }
    
    Server[] server = new Server[multiProperties.size()] ;
    int idx = 0;
    for(Map<String, String> properties : multiProperties.values()) {
      server[idx++] = Server.create(properties) ;
    }
    return server ;
  }
  
  static public void main(String[] args) throws Exception {
    create(args) ;
    Thread.currentThread().join();
  }
}
