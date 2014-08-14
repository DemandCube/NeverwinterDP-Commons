package com.neverwinterdp.netty.multicast;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import com.neverwinterdp.netty.multicast.MulticastServer;
import com.neverwinterdp.netty.multicast.UDPClient;

/**
 * Test to ensure the single message broadcast works for the multicast server
 * @author rcduar
 *
 */
public class MulticastServerSingleResponseTest {
	private MulticastServer server ;
	private String expectedResponse = "Neverwinter Rocks!";
	private int port = 1111;
	
	/**
	 * Set up server to respond to any request with expectedResponse
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		server = new MulticastServer(port, expectedResponse);
	    new Thread() {
	      public void run() {
	        try {
	          server.run() ;
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	      }
	    }.start() ;
	    Thread.sleep(1000);
	  }
	  
	/**
	 * Tear down server at end of test
	 */
	  @After
	  public void teardown() {
		  try{
			  server.stop();
		  }
		  catch(Exception e){
			  e.printStackTrace();
		  }
	  }
	  
	  /**
	   * Ensure expectedResponse is received for any UDP message
	   * @throws Exception
	   */
	  @Test
	  public void testSingleResponseBroadcastServer() throws Exception {
		  UDPClient x = new UDPClient("localhost",port); 
		  String received = x.sendMessage("Hi UDP Server!");
		  assertEquals(expectedResponse, received);
	  }
	  
	  /**
	   * Ensure expectedResponse is received for any UDP message
	   * Run 100 times
	   * @throws Exception
	   */
	  @Test
	  public void testSingleResponseBroadcastServer100Times() throws Exception {
		  UDPClient x = new UDPClient("localhost",port); 
		  for(int i=0; i<100; i++){
			  String received = x.sendMessage("Hi UDP Server!");
			  assertEquals(expectedResponse, received);
		  }
	  }
}