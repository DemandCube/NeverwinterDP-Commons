package com.neverwinterdp.server.gateway;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.neverwinterdp.server.ServerRegistration;
import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.server.cluster.ClusterRegistration;
import com.neverwinterdp.server.cluster.hazelcast.HazelcastClusterClient;
import com.neverwinterdp.util.JSONSerializer;


public class ClusterGateway {
  private ClusterClient clusterClient ;
  
  public ClusterPlugin cluster ;
  public ServerCommandPlugin server ;
  public ModuleCommandPlugin module ;
  private Map<String, CommandPlugin> plugins ;
  
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
    server  = new ServerCommandPlugin() ;
    module  = new ModuleCommandPlugin() ;
    plugins = CommandPlugin.Util.loadByAnnotation("com.neverwinterdp.server.gateway") ;
    plugins.put("cluster", cluster) ;
    plugins.put("server", server) ;
    plugins.put("module", module) ;
    for(CommandPlugin plugin : plugins.values()) {
      plugin.init(clusterClient);
    }
  }
  
  public <T extends CommandPlugin> T plugin(String name) { return (T) plugins.get(name) ; }
  
  public String call(String argLine) {
    return call(new Command(argLine)) ;
  }
  
  public String call(Command cmd) {
    try {
      CommandPlugin plugin = plugin(cmd.getCommand()) ;
      return plugin.call(cmd) ;
    } catch(Throwable t) {
      Map<String, Object> result = new HashMap<String, Object>() ;
      result.put("success", false) ;
      StringWriter w = new StringWriter() ;
      t.printStackTrace(new PrintWriter(w));; 
      result.put("message", w.toString()) ;
      return JSONSerializer.INSTANCE.toString(result) ;
    }
  }
  
  public <T> T execute(Command cmd) throws Exception {
    CommandPlugin plugin = plugin(cmd.getCommand()) ;
    return plugin.execute(cmd) ;
  }
  
  public <T> T execute(String commandLine) throws Exception {
    return execute(new Command(commandLine)) ;
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
  
  public void close() {
    if(clusterClient != null) clusterClient.shutdown(); 
  }
}