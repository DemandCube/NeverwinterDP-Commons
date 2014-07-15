package com.neverwinterdp.server.gateway;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandParams extends HashMap<String, Object> {
  
  public CommandParams()  {
    
  }

  public CommandParams field(String name, Object value) {
    put(name, value) ;
    return this ;
  }
  
  public String getString(String name) {
    return (String) get(name) ;
  }
  
  public int getInt(String name) {
    return (int) get(name) ;
  }
  
  public long getLong(String name, long dval) {
    if(!this.containsKey(name)) return dval ;
    Object val = get(name) ;
    if(val instanceof Integer) return (long) ((Integer)val).longValue() ;
    else if(val instanceof String) return Long.parseLong((String)val);
    return (long) get(name) ;
  }
  
  public double getDouble(String name, double dval) {
    if(!this.containsKey(name)) return dval ;
    return (double) get(name) ;
  }
  
  public boolean getBoolean(String name, boolean dval) {
    if(!this.containsKey(name)) return dval ;
    return (boolean) get(name) ;
  }
  
  public List<String> getStringList(String name) {
    Object val = get(name) ;
    if(val instanceof String[]) {
      return Arrays.asList((String[]) val) ;
    }
    return (List<String>) val ;
  }
  
  public Map<String, String> getProperties() {
    Map<String, String> properties = new HashMap<String, String>() ;
    for(Map.Entry<String, Object> entry : entrySet()) {
      String key = entry.getKey() ;
      if(!key.startsWith("-P")) continue ;
      key = key.substring(2) ;
      properties.put(key, (String) entry.getValue()) ;
    }
    return properties ;
  }
  
  public String[] getArguments() {
    List<String> holder = new ArrayList<String>();
    for(Map.Entry<String, Object> entry : entrySet()) {
      String key = entry.getKey() ;
      int colon = key.indexOf(':') ;
      if(key.startsWith("-")) {
        if(colon > 0) {
          holder.add(key + "=" + entry.getValue()) ;
        } else {
          holder.add(key) ;
          holder.add(entry.getValue().toString()) ;
        }
      } else {
        if(colon > 0) {
          holder.add("--" + key + "=" + entry.getValue()) ;
        } else {
          holder.add("--" + key) ;
          holder.add(entry.getValue().toString()) ;
        }
      }
    }
    return holder.toArray(new String[holder.size()]) ;
  }
}
