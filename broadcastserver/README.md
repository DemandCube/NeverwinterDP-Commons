#BROADCAST SERVER#

The broadcast server is a highly available, redundant UDP server for serving out 
IP/Hostname:Port configurations for autodiscovery configurations

The Autodiscovery broadcast server.  Uses zookeeper to remain highly available and 
listens on a port (1111) by default
Upon receipt of a broadcasted udp packet, the server will automatically reponsd
 
The -propertiesFile option (default is broadcast.properties) is a Java properties file
with numerous key:value pairs.  Example properties file:
```
dev=2.2.2.2:2181,2.2.2.3:2181
prod=1.1.1.2:2181,1.1.2.3:2181
local=127.0.0.1:2181,127.0.0.1:2181
broadcast=localhost:2181
```
`
Using the above file, when sent a UDP packet with the payload "dev" the server will respond to 
the sender with "2.2.2.2:2181,2.2.2.3:2181"
  
The "broadcast" key is required if the -broadcastZookeeper option is not given.  This is the 
zookeeper that the broadcastServer will connect to in order to stay highly available.  All 
broadcastServers that connect to that zookeeper cluster will attempt to become master, but 
only one server will remain as master.  Only the master node will launch the UDP server

Check the main() method of this class for an example usage

##Configuration##
The server will read in a file, by default is ./broadcast.properties
broadcast.properties Must have an entry for broadcast itself
```
dev=2.2.2.2:2181,2.2.2.3:2181
prod=1.1.1.2:2181,1.1.1.3:2181,1.1.1.4:2181,1.1.1.5:2998
local=127.0.0.1:2181

#broadcast is reserved for the broadcast server itself.  
#This is the zookeeper instance that broadcast will connect to
broadcast=localhost:2181
```

In the above configuration, Broadcast will connect to the zookeeper at localhost:2181


##Using##
To get info from the Broadcast server, open a UDP connection to the server.
Sending the key string will return the value.
Example using the configuration above (assuming broadcast is running on localhost with port 1111):
```
#>echo "dev" | nc -u localhost 1111
2.2.2.2:2181,2.2.2.3:2181
#>echo "prod" | nc -u localhost 1111
1.1.1.2:2181,1.1.1.3:2181,1.1.1.4:2181,1.1.1.5:2998
#>echo "local" | nc -u localhost 1111
127.0.0.1:2181
#>echo "broadcast" | nc -u localhost 1111
localhost:2181
```

##Running The Server##
```
java Broadcast  -broadcastZookeeper VAL -help -propertiesFile VAL -udpPort N
 -broadcastZookeeper VAL : The zookeeper [host]:[port] for this server to
                           connect to (overrides "broadcast" key in properties file
 -help                   : Displays help message
 -propertiesFile VAL     : Java properties file to read in
 -udpPort N              : UDP port to run Broadcast server on
```
##Build And Develop##

###Build With Gradle###

1. cd NeverwinterDP-Commons
2. gradle clean build install

###Eclipse###

To generate the eclipse configuration

1. cd NeverwinterDP/code
2. gradle eclipse

To import the project into the  eclipse

1. Choose File > Import
2. Choose General > Existing Projects into Workspace
3. Check Select root directory and browse to path/NeverwinterDP-Commons
4. Select all the projects  then click Finish.