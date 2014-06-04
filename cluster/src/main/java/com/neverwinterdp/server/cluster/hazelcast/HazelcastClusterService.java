package com.neverwinterdp.server.cluster.hazelcast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.codahale.metrics.Timer;
import com.google.inject.Singleton;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Member;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.neverwinterdp.server.ActivityLog;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.ServerRegistration;
import com.neverwinterdp.server.cluster.ClusterEvent;
import com.neverwinterdp.server.cluster.ClusterListener;
import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.server.cluster.ClusterService;
import com.neverwinterdp.server.cluster.ClusterRegistraton;
import com.neverwinterdp.server.command.ServerCommand;
import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.command.ServiceCommand;
import com.neverwinterdp.server.command.ServiceCommandResult;
import com.neverwinterdp.server.monitor.MonitorRegistry;
import com.neverwinterdp.util.LoggerFactory;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
@Singleton
public class HazelcastClusterService implements ClusterService, MessageListener<ClusterEvent>  {
  static Map<String, HazelcastClusterService> instances = new HashMap<String, HazelcastClusterService>() ;
  
  private Logger logger ;
  private HazelcastInstance hzinstance ;
  private ClusterMember member ;
  private ClusterRegistraton clusterRegistration ;
  private Server server ;
  private MonitorRegistry registry ;
  private List<ClusterListener<Server>> listeners = new ArrayList<ClusterListener<Server>>() ;
  private ITopic<ClusterEvent> clusterEventTopic ;
  private String               clusterEventTopicListenerId ;
  
  public HazelcastClusterService() {
    Config config = new XmlConfigBuilder().build();
    hzinstance = Hazelcast.newHazelcastInstance(config);
    Member hzmember= hzinstance.getCluster().getLocalMember() ;
    member = new ClusterMemberImpl(hzmember) ;
    synchronized(instances) {
      instances.put(hzinstance.getName(), this) ;
    }
  }
  
  public void setMonitorRegistry(MonitorRegistry registry) {
    this.registry = registry ;
  }
 
  public void setLoggerFactory(LoggerFactory factory) {
    logger = factory.getLogger(getClass().getName()) ;
  }
 
  
  public void onInit(Server server) {
    this.server = server ;
    clusterEventTopic = hzinstance.getTopic(CLUSTER_EVENT_TOPIC);
    clusterEventTopicListenerId = clusterEventTopic.addMessageListener(this) ;
    IMap<String, ServerRegistration> registrationMap = hzinstance.getMap(CLUSTER_REGISTRATON) ;
    clusterRegistration = new ClusterRegistrationImpl(registrationMap) ;
  }
  
  public void onDestroy(Server server) {
    synchronized(instances) {
      instances.remove(hzinstance.getName()) ;
    }
    clusterEventTopic.removeMessageListener(clusterEventTopicListenerId) ;
    hzinstance.shutdown(); 
  }
  
  public Server getServer() { return this.server ; }
  
  public ClusterMember getMember() { return member ; }
  
  public ClusterRegistraton getClusterRegistration() { return clusterRegistration ; }
  
  public void updateClusterRegistration() {
    clusterRegistration.update(server.getServerRegistration());
  }
  
  public void addClusterListener(ClusterListener<Server> listener) {
    listeners.add(listener) ;
  }
  
  public <T> ServiceCommandResult<T>  execute(ServiceCommand<T> command, ClusterMember member) {
    return Util.submit(hzinstance, command, member) ;
  }
  
  public <T> ServiceCommandResult<T>[] execute(ServiceCommand<T> command, ClusterMember[] member) {
    return Util.submit(hzinstance, command, member) ;
  }
  
  public <T> ServiceCommandResult<T> [] execute(ServiceCommand<T> command) {
    return Util.submit(hzinstance, command) ;
  }
  
  public <T> ServerCommandResult<T> execute(ServerCommand<T> command, ClusterMember member) {
    return Util.submit(hzinstance, command, member) ;
  }
  
  public <T> ServerCommandResult<T>[] execute(ServerCommand<T> command, ClusterMember[] member) {
    return Util.submit(hzinstance, command, member) ;
  }
  
  public <T> ServerCommandResult<T>[] execute(ServerCommand<T> command) {
    return Util.submit(hzinstance, command) ;
  }
  
  public void broadcast(ClusterEvent event) {
    logger.info("Start broadcast(event), event = " + event.getType());
    event.setSourceMember(member);
    clusterEventTopic.publish(event);
    logger.info("Finish broadcast(event), event = " + event.getType());
  }

  public void onMessage(Message<ClusterEvent> message) {
    long start = System.currentTimeMillis() ;
    ClusterEvent event = message.getMessageObject() ;
    Timer.Context timeCtx = registry.timer("event", event.getType().toString()).time() ;
    logger.info("Start onMessage(...), event = " + event.getType());
    for(int i = 0; i < listeners.size(); i++) {
      ClusterListener<Server> listener = listeners.get(i) ;
      listener.onEvent(server, event) ;
    }
    long end = System.currentTimeMillis() ;
    String msg = "Received an event " +  event.getType() + " " + event.getSource() + " from " + event.getSourceMember().toString();
    String activityLogName = event.getType().toString() ;
    timeCtx.stop() ;
    ActivityLog log = new ActivityLog(activityLogName, ActivityLog.Type.ClusterEvent, start, end, msg) ;
    server.getActivityLogs().add(log);
    logger.info(log.toString());
    logger.info("Finish onMessage(...), event = " + event.getType());
  }
  
  static public HazelcastClusterService getClusterRPC(HazelcastInstance hzinstance) {
    return instances.get(hzinstance.getName()) ;
  }
}