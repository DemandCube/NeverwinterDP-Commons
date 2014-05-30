package com.neverwinterdp.server.cluster.hazelcast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Member;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.neverwinterdp.server.ServerRegistration;
import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.cluster.ClusterEvent;
import com.neverwinterdp.server.cluster.ClusterListener;
import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.server.cluster.ClusterService;
import com.neverwinterdp.server.cluster.ClusterRegistraton;
import com.neverwinterdp.server.command.ServerCommand;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.command.ServerCommands;
import com.neverwinterdp.server.command.ServiceCommand;
import com.neverwinterdp.server.command.ServiceCommandResult;
import com.neverwinterdp.util.text.StringUtil;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class HazelcastClusterClient implements ClusterClient,  MessageListener<ClusterEvent>  {
  private HazelcastInstance hzclient ;
  private ClusterRegistraton clusterRegistration ;
  private List<ClusterListener<ClusterClient>> listeners = new ArrayList<ClusterListener<ClusterClient>>() ;
  private ITopic<ClusterEvent> clusterEventTopic ;
  private String               clusterEventTopicListenerId ;
  
  public HazelcastClusterClient() {
    this(new String[0]) ;
  }
  
  public HazelcastClusterClient(String ... connectAddress) {
    ClientConfig config = new ClientConfig();
    if(connectAddress != null && connectAddress.length > 0) {
      config.getNetworkConfig().addAddress(connectAddress);
    }
    config.getGroupConfig().setName("neverwinterdp");
    config.getGroupConfig().setPassword("neverwinterdp");
    hzclient = HazelcastClient.newHazelcastClient(config);
    
    IMap<String, ServerRegistration> registrationMap = hzclient.getMap(ClusterService.CLUSTER_REGISTRATON) ;
    clusterRegistration = new ClusterRegistrationImpl(registrationMap) ;
    
    clusterEventTopic = hzclient.getTopic(ClusterService.CLUSTER_EVENT_TOPIC);
    clusterEventTopicListenerId = clusterEventTopic.addMessageListener(this) ;
  }
  
  public ClusterRegistraton getClusterRegistration() {
    return clusterRegistration ;
  }
  
  public ClusterMember getClusterMember(String connect) {
    int    port = 5700 ;
    String host = connect ;
    if(connect.indexOf(':') > 0) {
      String[] parts = StringUtil.toStringArray(connect, ":") ;
      host = parts[0] ;
      port = Integer.parseInt(parts[1]) ;
    }
    Set<Member> members = hzclient.getCluster().getMembers() ;
    for(Member sel : members) {
      ClusterMember cmember = new ClusterMemberImpl(sel) ;
      if(host.equals(cmember.getHost()) || host.equals(cmember.getIpAddress())) {
        if(port == cmember.getPort()) return cmember ;
      }
    }
    return null ;
  }
  
  public void addListener(ClusterListener<ClusterClient> listener) {
    listeners.add(listener) ;
  }
  
  public void removeListener(ClusterListener<ClusterClient> listener) {
    listeners.add(listener) ;
  }
  
  public ServerRegistration getServerRegistration(ClusterMember member) {
    ServerCommand<ServerRegistration> cmd = new ServerCommands.GetServerRegistration() ;
    ServerCommandResult<ServerRegistration> result = execute(cmd, member) ;
    return result.getResult() ;
  }
  
  public <T> ServiceCommandResult<T>  execute(ServiceCommand<T> command, ClusterMember member) {
    return Util.submit(hzclient, command, member) ;
  }
  
  public <T> ServiceCommandResult<T>[] execute(ServiceCommand<T> command, ClusterMember[] member) {
    return Util.submit(hzclient, command, member) ;
  }
  
  public <T> ServiceCommandResult<T> [] execute(ServiceCommand<T> command) {
    return Util.submit(hzclient, command) ;
  }

  public <T> ServerCommandResult<T> execute(ServerCommand<T> command, ClusterMember member) {
    return Util.submit(hzclient, command, member) ;
  }
  
  public <T> ServerCommandResult<T>[] execute(ServerCommand<T> command, ClusterMember[] member) {
    return Util.submit(hzclient, command, member) ;
  }
  
  public <T> ServerCommandResult<T>[] execute(ServerCommand<T> command) {
    return Util.submit(hzclient, command) ;
  }
  
  public void broadcast(ClusterEvent event) {
    clusterEventTopic.publish(event);
  }

  public void onMessage(Message<ClusterEvent> message) {
    ClusterEvent event = message.getMessageObject() ;
    for(int i = 0; i < listeners.size(); i++) {
      ClusterListener<ClusterClient> listener = listeners.get(i) ;
      listener.onEvent(this, event) ;
    }
  }
  
  public void shutdown() {
    clusterEventTopic.removeMessageListener(clusterEventTopicListenerId) ;
    hzclient.shutdown();
  }
}