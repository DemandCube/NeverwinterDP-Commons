package com.neverwinterdp.server.gateway;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.neverwinterdp.server.ServerRegistration;
import com.neverwinterdp.server.ServerState;
import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.server.cluster.ClusterRegistration;
import com.neverwinterdp.server.cluster.hazelcast.HazelcastClusterClient;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.util.JSONSerializer;


public class ClusterGateway {
  private ClusterClient clusterClient ;
  
  public ClusterPlugin cluster ;
  public ServerPlugin server ;
  public ModulePlugin module ;
  private Map<String, Plugin> plugins ;
  
  public ClusterGateway() {
    connect() ;
  }
  
  public ClusterGateway(String ... connect) {
    connect(connect) ;
  }
  
  public void connect(String ... connect)  {
    if(clusterClient != null) clusterClient.shutdown(); 
    clusterClient = new HazelcastClusterClient(connect) ;
    cluster = new ClusterPlugin() ;
    server  = new ServerPlugin() ;
    module  = new ModulePlugin() ;
    plugins = Plugin.Util.loadByAnnotation("com.neverwinterdp.server.gateway") ;
    plugins.put("cluster", cluster) ;
    plugins.put("server", server) ;
    plugins.put("module", module) ;
    for(Plugin plugin : plugins.values()) {
      plugin.init(clusterClient);
    }
  }
  
  public <T extends Plugin> T plugin(String name) { return (T) plugins.get(name) ; }
  
  public String call(String group, String command, String jsonParams) {
    CommandParams params = JSONSerializer.INSTANCE.fromString(jsonParams, CommandParams.class) ;
    return call(group, command, params) ;
  }
  
  public String call(String group, String command, CommandParams params) {
    try {
      Plugin plugin = plugin(group) ;
      return plugin.call(command, params) ;
    } catch(Throwable t) {
      Map<String, Object> result = new HashMap<String, Object>() ;
      result.put("success", false) ;
      StringWriter w = new StringWriter() ;
      t.printStackTrace(new PrintWriter(w));; 
      result.put("message", w.toString()) ;
      return JSONSerializer.INSTANCE.toString(result) ;
    }
  }
  
  public Object execute(String group, String command, CommandParams params) {
    try {
      Plugin plugin = plugin(group) ;
      return plugin.execute(command, params) ;
    } catch(Throwable t) {
      Map<String, Object> result = new HashMap<String, Object>() ;
      result.put("success", false) ;
      StringWriter w = new StringWriter() ;
      t.printStackTrace(new PrintWriter(w));; 
      result.put("message", w.toString()) ;
      return JSONSerializer.INSTANCE.toString(result) ;
    }
  }
  
  public String getMembers() {
    ClusterRegistration reg = clusterClient.getClusterRegistration() ;
    ServerRegistration[] sreg = reg.getServerRegistration() ;
    ClusterMember[] member = new ClusterMember[sreg.length] ;
    for(int i = 0; i < member.length; i++) member[i] = sreg[i].getClusterMember() ;
    return JSONSerializer.INSTANCE.toString(member) ;
  }
  
  public String clusterRegistration() {
    return  JSONSerializer.INSTANCE.toString(clusterClient.getClusterRegistration())  ;
  }
  
  public ClusterRegistration getClusterRegistration() {
    return  clusterClient.getClusterRegistration()  ;
  }
  
  public ClusterClient getClusterClient() { return this.clusterClient ; }
  
  public boolean waitForRunningMembers(MemberSelector selector, int numberOfMembers, long timeout) {
    ServerCommandResult<ServerState>[] results  = server.ping(selector) ;
    long stopTime = System.currentTimeMillis() + timeout ;
    while(System.currentTimeMillis() < stopTime) {
      boolean ok = true ;
      for(ServerCommandResult<ServerState> sel : results) {
        if(ServerState.RUNNING.equals(sel.getResult())) continue ;
        ok = false ;
        break ;
      }
      if(ok) return true ;
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
      }
    }
    return false ;
  }
  
  public void close() {
    if(clusterClient != null) clusterClient.shutdown(); 
  }
}