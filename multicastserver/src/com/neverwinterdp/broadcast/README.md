#BROADCAST SERVER#

The broadcast server is a highly available, redundant UDP server for serving out IP/Hostname:Port configurations for zookeeper configurations


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