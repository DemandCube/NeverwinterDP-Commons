package com.neverwinterdp.netty.multicast;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private Logger logger;
  
  /**
   * Constructor
   * @param msg The single, simple message to broadcast
   */
  public MulticastServerHandler(String msg){
    super();
    this.message = msg;
    this.logger = LoggerFactory.getLogger(getClass().getSimpleName()) ;
      logger.info("MulticastServerHandler initialized.  Response string is: "+msg);
  }
  
  /**
   * Constructor
   * @param msg The message to broadcast. Format is <Request String, Response String>
   */
  public MulticastServerHandler(Map<String, String> msg){
    super();
    
    //Unfortunately, we have to do a copy here to make this assignment work
    this.messageMap = new HashMap<String,String>(msg);
    this.logger = LoggerFactory.getLogger(getClass().getSimpleName()) ;
    logger.info("MulticastServerHandler initialized.  Response map is: "+msg.toString());
  }
  
  
  /**
   * Set the message map
   * @param message Message to broadcast
   */
  public void setMessageMap(Map<String,String> msgMap){
    logger.info("Setting response map to: "+msgMap.toString());
    this.messageMap = msgMap;
  }
  
  /**
   * Get the broadcast message
   * @return
   */
  public Map<String,String> getMessageMap(){
    logger.info("Returning messageMap");
    return this.messageMap;
  }
  
  
  /**
   * Set the message
   * @param message Message to broadcast
   */
  public void setMessage(String message){
    logger.info("Setting message to: "+message);
    this.message = message;
  }
  
  /**
   * Get the broadcast message
   * @return
   */
  public String getMessage(){
    logger.info("Returning message");
    return this.message;
  }
  
  /**
   * Handles incoming message
   */
  @Override
  public void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
    logger.info("Handling incoming message");
    String toSend="";
    
    //If messageMap is null, then we only have the simple message to send
    if(null == this.messageMap){
      logger.info("Using simple message");
      toSend = this.message;
    }
    //Otherwise figure out if messageMap has the key that's been sent in
    else{
      //Get message contents, trim the fat
      String x = packet.content().toString(CharsetUtil.UTF_8).trim();
      
      //If the map contains that key, broadcast the value
      if(this.messageMap.containsKey(x)){
        logger.info("Returning info for key:"+x);
        toSend = this.messageMap.get(x);
      }
      //Return ERROR if the key doesn't exist
      else{
        logger.info("key not found in message map: "+x);
        toSend= "ERROR";
      }
    }
    

    logger.info("Packet payload is:"+toSend);
    //Send packet
    ctx.write(new DatagramPacket(Unpooled.copiedBuffer(toSend, CharsetUtil.UTF_8), packet.sender()));
  }
  
  /**
   * Boiler plate code
   */
  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    logger.info("Flushing");
    ctx.flush();
  }
  
  /**
   * What to do if there's an exception.
   * We don't want to stop serving if there's an error
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.info("Exception caught: "+cause.getMessage());
    // We don't close the channel because we can keep serving requests.
    cause.printStackTrace();
  }
}
