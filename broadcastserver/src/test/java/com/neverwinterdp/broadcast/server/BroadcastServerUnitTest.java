package com.neverwinterdp.broadcast.server;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.netty.multicast.UDPClient;
import com.neverwinterdp.zookeeper.cluster.ZookeeperClusterBuilder;
import com.neverwinterdp.broadcast.server.BroadcastServer;

public class BroadcastServerUnitTest {
  static ZookeeperClusterBuilder clusterBuilder ;
  static String connection;
  static BroadcastServer server;
  static BroadcastServer server2;
  static Map<String, String> map = new HashMap<String, String>();
  static int port = 1120;
  static String tempFileName="b.prop.tmp";
  
  /**
   * Build zookeeper server
   * @throws Exception
   */
  @BeforeClass
  static public void setup() throws Exception {
    connection = "127.0.0.1:2181";
    clusterBuilder = new ZookeeperClusterBuilder() ;
    clusterBuilder.install();
    Thread.sleep(3000);
      
      
    map.put("dev", "2.2.2.2:2181,2.2.2.3:2181");
    map.put("local", "127.0.0.1:2181,127.0.0.1:2181");
    map.put("prod","1.1.1.2:2181,1.1.2.3:2181");
    map.put("broadcast", "localhost:2181");
      
      
    File tempFile = new File(tempFileName);
    tempFile.deleteOnExit();
    System.out.println("TempFile:"+tempFile.getAbsolutePath());
    FileWriter fileWriter = new FileWriter(tempFile, false);
    BufferedWriter bw = new BufferedWriter(fileWriter);
    
    bw.write("dev=2.2.2.2:2181,2.2.2.3:2181\nprod=1.1.1.2:2181,1.1.2.3:2181\nlocal=127.0.0.1:2181,127.0.0.1:2181\nbroadcast=localhost:2181\n");
    bw.close();
    
    String[] broadcastArgs = {
      "-propertiesFile", tempFile.getAbsolutePath(), "-udpPort", Integer.toString(port)
    };
    
    server = new BroadcastServer( broadcastArgs);
    assertTrue(server.initialize());

    server.startServer();
    while(!server.isBroadcastServerRunning()){
      Thread.sleep(500);
    }
  }

  /**
   * Destroy zookeeper server and broadcast server
   * @throws Exception
   */
  @AfterClass
  static public void teardown() throws Exception {
    server.stopServer();
    server2.stopServer();
    clusterBuilder.destroy();
  }
  
  @Test(timeout=60000)
  public void testServerIsMaster() throws InterruptedException{
    assertTrue(server.isMaster());
  }
  
  /**
   * Test that each key in the hash map returns the correct value 100 times and that an
   * invalid key returns "ERROR"
   * @throws Exception
   */
  @Test(timeout=60000)
  public void testServerReturnsCorrectInfo100Times() throws InterruptedException{
    UDPClient x = new UDPClient("localhost",port); 
    for(int i=0; i<100; i++){
      for (Map.Entry<String, String> entry : map.entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();
        String received = x.sendMessage(key);
        assertEquals(value, received);
      }
      String received = x.sendMessage("Force an error!");
      assertEquals("ERROR", received);
    }
  }
  
  /**
   * Test that each key in the hash map returns the correct value and that an
   * invalid key returns "ERROR"
   * @throws Exception
   */
  @Test(timeout=60000)
  public void testServerReturnsCorrectInfo() throws Exception {
    UDPClient x = new UDPClient("localhost",port); 
    for (Map.Entry<String, String> entry : map.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      String received = x.sendMessage(key);
      assertEquals(value, received);
    }
    String received = x.sendMessage("Force an error!");
    assertEquals("ERROR", received);
  }
  
  /**
   * Opens another Broadcast Server, ensures it is not master and not running the Broadcast server
   * @throws IOException 
   * @throws InterruptedException 
   */
  @Test(timeout=60000)
  public void testSecondBroadcasterIsNotMaster() throws IOException, InterruptedException{
    String testTempFileName = tempFileName+".tstSecondBroadcasterIsNotMaster"; 
      
    File tempFile = new File(testTempFileName);
    tempFile.deleteOnExit();
    FileWriter fileWriter = new FileWriter(tempFile, false);
    BufferedWriter bw = new BufferedWriter(fileWriter);
    bw.write("dev=2.2.2.2:2181,2.2.2.3:2181\nprod=1.1.1.2:2181,1.1.2.3:2181\nlocal=127.0.0.1:2181,127.0.0.1:2181\nbroadcast=localhost:2181\n");
    bw.close();
    
    
    String[] broadcastArgs = new String[2];
    broadcastArgs[0] = "-propertiesFile";
    broadcastArgs[1] = tempFile.getAbsolutePath();
    
    server2 = new BroadcastServer( broadcastArgs);
    assertTrue(server2.initialize());
    new Thread() {
      public void run() {
        try{
          server2.runServerLoop() ;
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    }.start() ;
      
    Thread.sleep(10000);
    
    assertFalse(server2.isBroadcastServerRunning());
    assertFalse(server2.isMaster());
    server2.stopServer();    
  }
}
