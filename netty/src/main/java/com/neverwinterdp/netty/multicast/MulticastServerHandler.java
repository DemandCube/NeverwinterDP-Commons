package com.neverwinterdp.netty.multicast;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;


/**
 * Handler class for Multicast.  More in depth usage is found in MulticastServer.java
 * @author Richard Duarte
 * 
 */
public class MulticastServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	
	private String message="";
	private Map<String,String> messageMap=null;
	
	/**
	 * Constructor
	 * @param msg The single, simple message to broadcast
	 */
	public MulticastServerHandler(String msg){
		super();
		this.message = msg;
	}
	
	/**
	 * Constructor
	 * @param msg The message to broadcast. Format is <Request String, Response String>
	 */
	public MulticastServerHandler(Map<String, String> msg){
		super();
		
		//Unfortunately, we have to do a copy here to make this assignment work
		this.messageMap = new HashMap<String,String>(msg);
	}
	
	
	/**
	 * Set the message map
	 * @param message Message to broadcast
	 */
	public void setMessageMap(Map<String,String> msgMap){
		this.messageMap = msgMap;
	}
	
	/**
	 * Get the broadcast message
	 * @return
	 */
	public Map<String,String> getMessageMap(){
		return this.messageMap;
	}
	
	
	/**
	 * Set the message
	 * @param message Message to broadcast
	 */
	public void setMessage(String message){
		this.message = message;
	}
	
	/**
	 * Get the broadcast message
	 * @return
	 */
	public String getMessage(){
		return this.message;
	}
	
	/**
	 * Writes message to UDP port
	 */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
		String toSend="";
		
		//If messageMap is null, then we only have the simple message to send
		if(null == this.messageMap){
			toSend = this.message;
		}
		//Otherwise figure out if messageMap has the key that's been sent in
		else{
			//Get message contents, trim the fat
			String x = packet.content().toString(CharsetUtil.UTF_8).trim();
			
			//If the map contains that key, broadcast the value
			if(this.messageMap.containsKey(x)){
				toSend = this.messageMap.get(x);
			}
			//Return ERROR if the key doesn't exist
			else{
				toSend= "ERROR";
			}
		}
		
		//Send packet
		ctx.write(new DatagramPacket(Unpooled.copiedBuffer(toSend, CharsetUtil.UTF_8), packet.sender()));
	}
	
	/**
	 * Boiler plate code
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}
	
	/**
	 * What to do if there's an exception.
	 * We don't want to stop serving if there's an error
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// We don't close the channel because we can keep serving requests.
		cause.printStackTrace();
	}
}
