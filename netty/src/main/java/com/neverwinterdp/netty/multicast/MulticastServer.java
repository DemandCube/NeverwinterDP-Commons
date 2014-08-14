package com.neverwinterdp.netty.multicast;

import java.util.HashMap;
import java.util.Map;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;


/**
 * Simple server to print out a message over UDP over a configured port
 * 
 * Example 1: Broadcasting a single message:
 * 	This will open up a server on port 1111
 * 	Any time somebody sends your server a UDP message on that port, it will respond with "Neverwinter Rocks!"
 * 		MulticastServer broadcaster = new MulticastServer(1111, "Neverwinter Rocks!");
 * 		broadcaster.run();
 * 		//Likely you'll never reach stop() since run() blocks execution
 * 		broadcaster.stop();
 * 
 * 
 * ****************************************************
 * 
 * Example 2: Broadcasting a map of messages:
 * 
 * 	This will open up a server on port 111
 * 
 * 	Any time somebody sends your server a UDP message on that port, 
 * 	it will read in the message and determine if that message is a key in the map
 * 	If it is a key in the map, it will return the associated value
 * 	
 * Otherwise it will return the string "ERROR"
 * 
 * 	In this specific example, if you sent a UDP message to the server on port 1111 
 * 	with the string "dev" in the payload,
 * 	It would return the string "1.1.1.1:8080"
 * 	
 * 		Map<String, String> m = new HashMap<String, String>();
 * 		m.put("dev", "1.1.1.1:8080");
 * 		m.put("local", "2.2.2.2:1111");
 * 		m.put("prod","3.3.3.3:1234,3.3.3.4:1234,3.3.3.3:1234");
 * 		MulticastServer broadcaster = new MulticastServer(1111, m);
 * 		broadcaster.run();
 * 		broadcaster.stop();  //Likely will never reach here in this example
 * 
 * *****************************************************
 * 
 * To test your server, you can use netcat:
 * #> nc -u [hostname/localhost] [port number]
 * 
 * *****************************************************
 * 
 * @author Richard Duarte
 *
 */
public class MulticastServer{
	private int port;
	private EventLoopGroup group=null;
	String message="";
	Map<String, String> messageMap = new HashMap<String, String>();
	
	/**
	 * Constructor to broadcast a single, simple string
	 * @param port Port to run on
	 * @param msg Message to broadcast
	 */
	public MulticastServer(int port, String msg){
		this.port = port;
		this.message = msg;
	}
	
	/**
	 * Constructor to use message map
	 * @param port Port to run on 
	 * @param msg Map of <String,String> Where format is <Request String, Response String>
	 */
	public MulticastServer(int port, Map<String, String> msg){
		this.port = port;
		//Have to do a deep copy here
		this.messageMap = new HashMap<String,String>(msg);
	}
	
	
	/**
	 * Start the broadcasting UDP server
	 */
	public void run(){
		this.group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			
			//If messageMap is empty, 
			//then we just have the single, simple string to broadcast
			if(this.messageMap.isEmpty()){
				b.group(group)
					.channel(NioDatagramChannel.class)
					.option(ChannelOption.SO_BROADCAST, true)
					.handler(new MulticastServerHandler(this.message));
			}
			
			//Otherwise we use the other constructor
			//and pass in the messageMap
			else{
				b.group(group)
				.channel(NioDatagramChannel.class)
				.option(ChannelOption.SO_BROADCAST, true)
				.handler(new MulticastServerHandler(this.messageMap));
			}

			//Bind server
			b.bind(this.port).sync().channel().closeFuture().await();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			group.shutdownGracefully();
		}
	}
	
	public void stop(){
		this.group.shutdownGracefully();
	}
}
