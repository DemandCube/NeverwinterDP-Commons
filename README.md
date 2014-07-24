#NeverwinterDP-Commons#

This project contains the shared and reusable code for the other component such Queuengin, Sparkngin, Scribengin, DemandSpike...

#Code Organization#

**Directory Structure**

```
  NeverwinterDP-Commons
    -  api
    -  utils
    -  cluster
    -  hadoop-framework
    -  netty
    -  elasticsearch
```

1. The api project contains the common classes and interfaces for the message protocol and service.
2. The utils contains the utility class such IOUtil, FileUtil, StringUtil, JVM...
3. The cluster project is implemented on top of the hazelcast project. It is designed to manage multiple servers and services from a single point. 
4. The hadoop-framework project contains a yarn app framework with the RPC communication. It also contains hadoop sample such wordcount , and sample for  hadoop mini cluster unit test.
5. The netty project currently implements a http server on top of the netty project. It also implement and http cluster service which is manageable by the cluster management tool. 
6. The elasticsearch project implements a wrapper service which is manageable by the cluster management tool. 

#Message Protocol#

##Overrall Design##

```
 +------------+
 |Rest Client |  
  +-----------+   +-------------+    +---------------+    +------------+    +------------+
          |       |Rest         |    |Persistent     |    |Data        |    |Data        |
          +-----> | Endpoint    |+-->| Queuengin     |+-->| Distributor|+-->| Sink       |
          |       |(Sparkngin)  |    |(Kafka/Kinesis)|    |(Scribengin)|    |(Hive/Hbase)|
 +------------+   +-------------+    +---------------+    +------------+    +------------+
 |Demandspike |
 +------------+
```
 
##Message and Message Service##

From the overral design, we can consider each engine(client, Sparkngin, Scribengin, Data Sink) as a message service where the message is forwarded to each service , process and then forward to the next service point. At each service point, the service can reject the message due to the message error, save the message to retry later due to the next service point is not available

**The message structure**

The message structure should:

  - The message should be generic and hold any type of event/object
  - The message should be able to hold the log of the activities such:
      + client send the message to http server. 
      + http server receive the message.
      + http server forward the message to kafka queue
      + scribe engine dequeue the message
      + scribe engine write message to hbase.
        ....
  - The message should be able to hold a list of instructions so each service point can pick up the instruction and execute the instruction before it processes the message. For example the http service can pick up an instruction and drop the message to generate a failed acknowledge and force the client to retry the message.

###Message API Proposal###
```
  public class Message {
    private MessageHeader            header = new MessageHeader();
    private MessageData              data;
    private List<MessageTrace>       traces;
    private List<MessageInstruction> instructions;
  }

  public class MessageHeader {
    private float      version;
    private String     topic;
    private String     key;
    private boolean    traceEnable;
    private boolean    instructionEnable;
  }
  
  public class MessageData {
    static public enum SerializeType { json, xml, binary }
  
    private String        type;
    private byte[]        data;
    private SerializeType serializeType;
  }
  
  public class MessageTrace {
    private String host      ;
    private String serviceId ;
    private float  serviceVersion ;
    private long   processTime ;
    private String message ;
  }
  
  public class MessageInstruction {
    private String targetService ;
    private String instruction ;
    private Map<String, String> params ;
  }
```

###MessageService API Proposal###

```
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 * 
 *                         +-----------------+   +-----------------+   +-----------------+
 *                         | MessageServer   |   | MessageServer   |   | MessageServer   |
 *                         +-----------------+   +-----------------+   +-----------------+
 * +--------+              |                 |   |                 |   |                 |
 * | Client |---Message--->| MessageServices |-->| MessageServices |-->| MessageServices |
 * +--------+              |  (Sparkngin)    |   |   (Queuengin)   |   |  (Scribengin)   |
 *                         +-----------------+   +-----------------+   +-----------------+
 * MessageService is composed of several components that can be configured, reused or replaced by
 * the different implementation:
 * 1. MessageServiceDescriptor is the service configuration with the properties such service name,
 *    service id, service version, description, the topics that the service are listened to.
 * 2. MessageProcessor: the logic to process the message such forward to sink, 
 *    route to another topic...
 * 3. MessageServicePlugin is designed to reuse certain code and logic such add trace log to the 
 *    message, monitor... The plugin is called before and after the message is processed with 2 
 *    methods onPreProcess(Message) and onPostProcess(Message)
 * 3. MessageErrorHandler is designed to handle the exception when the MessageProcessor throw the 
 *    exceptions such Rejected, Error, Retry, Unknown. 
 */
public class MessageService {
  private MessageServiceDescriptor descriptor ;
  private MessageProcessor processor ;
  private List<MessageServicePlugin> messagePlugins ;
  private List<MessageErrorHandler>  errorHandlers ;
  
  public MessageServiceDescriptor getDescriptor() { return descriptor ; }
  public void setDescriptor(MessageServiceDescriptor descriptor) {
    this.descriptor = descriptor ;
  }
  
  public void onInit() {
    for(MessageServicePlugin plugin : messagePlugins) {
      plugin.onInit() ;
    }
  }
  
  public void onDestroy() {
    for(MessageServicePlugin plugin : messagePlugins) {
      plugin.onDestroy() ;
    }
  }
  
  void process(Message message) {
    try { 
      for(MessageServicePlugin plugin : messagePlugins) {
        plugin.onPreProcess(this, message);
      }
      processor.process(this, message) ;
      for(MessageServicePlugin plugin : messagePlugins) {
        plugin.onPostProcess(this, message);
      }
    } catch(MessageException ex) {
      Type type = ex.getType() ;
      if(Type.REJECTED.equals(type)) {
        for(MessageErrorHandler handler : errorHandlers) {
          handler.onReject(this, message);
        }
      } else if(Type.ERROR.equals(type)) {
        for(MessageErrorHandler handler : errorHandlers) {
          handler.onError(this, message);
        }
      } else {
        for(MessageErrorHandler handler : errorHandlers) {
          handler.onUnknown(this, message);
        }
      }
    }
  }
}
```
 
##Message Client##

###Requirement###

1. The rest client should be able to take in a list of the broker connection or retrieve the list of url from zookeeper.
2. The client should be able to send a message or a batch message in the synchronous mode or asychronous mode.
  - Synchronous: The client should be able to send a message or a batch or messages in json format to the server. The server will reply an ackknowledg in json format, depend on the acknowledge status, the client should continue , retry or give up.
  - Asynchronous: The client should be able to send a stream  of messages or a stream of a batch messages to the server. For each set of messages or batch of messages, the server will send back an ackowledge.  Depend on the status of the acknowledge, the client should continue, retry or move the stream of message to another url handler or give up.
3. The client shoudl select the url in round robin mode(or other algorithm) and send the message to the http server. If the the client fail to send to an url , it should pick another url and retry. 
4. Plug in, the client should be able to plug in the interceptor such debug and trace interceptor so each time a message is sent, retried or failed, the interceptor will add the log to the message.
5. In case the client send a message to the server, the server able to handle the message but cannot send back an acknowledge due to the system or network overloaded. How should we handle? What should be the try strategy and the max number of retry 


#Cluster#

##Cluster Concept##

Cluster  is composed of a set of  the servers and smart client

##Server Concept##

1.  A Server can have different roles  such master and slave or  worker, or in our case we will have a group of server with zookeeper, kafka, demandspike roles. 
2.  A Server  can communicate with the other members  by 2 methods :
      -  Directly send the request to the other members  in sync or async mode
      -  Publish an  event to the cluster event  queue so all the other members (servers and smart client) will
          get noticed
3.  A server has many other subcomponents such the server state, logger, cluster communication
     implementation, service container...

##Module And Service Concept##

1. A server can have several modules. The module can be installed and uninstalled dynamically at the runtime.
2. A module is a service container.
3. The service life cycle and access  are managed by the service container .
4. A service can be dynamically added, removed,  started, stopped 
5. A service can be a simple java component  or  embbeded complex java project such kafka, jetty...
6. A service  can be wrapper to other  program such kafka, mysql, nginx. Actually you can lauch the other program via java Process, if the program is java , it will be launched in another jvm environment.

##Smart Client Concept##
1.  A Smart Client is a part of the cluster and it has to be in the same cluster network.
2.  A smart client can directly communicate with the other server, broadcast or listen to the cluster  event as a normal server
3.  A smart client does not provide any services.

##Client concept##
1.  The client is designed to communicate from outside.
2.  The client cannot communicate directly or  broadcast/listen to the cluster event.
3.  The cluster has to support the client and it need a master server to handle the client request.
4.  The client is usually communicate with the master server in sync mode ,  the server will handle the request  or forward the request to ther other servers in the cluster.

##Cluster Gateway##

The cluster gateway is a low level smart client lib. it allows the developer connect to a cluster, listen to the cluster event or send a command to a server or a group of servers.

##Cluster Shell##

The cluster shell is a command line tool develop on top of of the cluster gateway. The cluster shell is composed of several command and each command itself have several subcommand. 

The command syntax is:

```
command subcommand [--param value]*
```

Each command has a common set of parameters

Member selector:

```
--member-name: use this parameter to send the command to a specific server identify by the server name
--member-uuid: use this parameter to send the command to a specific server identify by the uuid
--member-role: use this parameter to send the command to a group of servers
``` 


###cluster command###

1. clsuter registration: Return and print the list of servers with the installed modules and services

###server command###

1. server ping: Return and print the state of the running server
2. server registration: Return and print the installed module , service status of a server or a group of server
3. server metric: Return and print the metric in the table format
4. server metric-clear: Clear the metric
5. server shutdown: This command will stop all the services and uninstall all the module , services of a server. But the server is still running and communicate with the client.
3. server exit: Completely shutdown and exit the jvm. The client is no more able to communicate with the server.

###module command###

1. module list: This command list the available module and status on each server.
2. module install: This command will install a module on a server or a group of server with the given properties for each module.
3. module uninstall: This command stop the services that belong to a module and uninstall the module.

###service comand###

TODO: Implement command start, stop service....



#Build And Develop#

##Build With Gradle##

1. cd NeverwinterDP-Commons
2. gradle clean build install

##Eclipse##

To generate the eclipse configuration

1. cd NeverwinterDP/code
2. gradle eclipse

To import the project into the  eclipse

1. Choose File > Import
2. Choose General > Existing Projects into Workspace
3. Check Select root directory and browse to path/NeverwinterDP-Commons
4. Select all the projects  then click Finish

