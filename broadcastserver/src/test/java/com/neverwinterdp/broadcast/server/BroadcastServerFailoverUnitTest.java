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

import com.neverwinterdp.zookeeper.cluster.ZookeeperClusterBuilder;
import com.neverwinterdp.broadcast.server.BroadcastServer;

public class BroadcastServerFailoverUnitTest {
  static ZookeeperClusterBuilder clusterBuilder ;
  static String connection;
  static BroadcastServer server;
  static Map<String, String> map = new HashMap<String, String>();
  static int port =1130;
  static int port2=45000;
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
    
    String[] broadcastArgs = new String[4];
    broadcastArgs[0] = "-propertiesFile";
    broadcastArgs[1] = tempFile.getAbsolutePath();
    broadcastArgs[2] = "-udpPort";
    broadcastArgs[3] =  Integer.toString(port);
    
    server = new BroadcastServer( broadcastArgs);
    assertTrue(server.initialize());
    new Thread() {
      public void run() {
        try {
          server.runServerLoop() ;
        } 
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    }.start();
  }
  
  /**
   * Destroy zookeeper server and broadcast server
   * @throws Exception
   */
  @AfterClass
  static public void teardown() throws Exception {
    clusterBuilder.destroy();
  }
  
  
  
  /**
   * When one server stops, the second server should take over
   * It should not take over until the first server has died
   * @throws IOException 
   * @throws InterruptedException 
   */
  @Test(timeout=60000)
  public void testSecondServerTakesControlWhenFirstServerDies() throws IOException, InterruptedException{
    Thread.sleep(10000);
    
    String testTempFileName = tempFileName+".tstSecondBroadcasterIsNotMaster"; 
      
    File tempFile = new File(testTempFileName);
    tempFile.deleteOnExit();
    FileWriter fileWriter = new FileWriter(tempFile, false);
    BufferedWriter bw = new BufferedWriter(fileWriter);
    bw.write("dev=2.2.2.2:2181,2.2.2.3:2181\nprod=1.1.1.2:2181,1.1.2.3:2181\nlocal=127.0.0.1:2181,127.0.0.1:2181\nbroadcast=localhost:2181\n");
    bw.close();
    
    
    String[] broadcastArgs = new String[4];
    
    broadcastArgs[0] = "-propertiesFile";
    broadcastArgs[1] = tempFile.getAbsolutePath();
    
     //Likely the UDP server won't start, 
    //netty doesn't like to grab this 2nd port
    broadcastArgs[2] = "-udpPort";
    broadcastArgs[3] =  Integer.toString(port2);
    
    final BroadcastServer server2 = new BroadcastServer( broadcastArgs);
    assertTrue(server2.initialize());
    new Thread() {
      public void run() {
        try {
          server2.runServerLoop() ;
        } 
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    }.start() ;
        
    Thread.sleep(5000);
    
    assertFalse(server2.isBroadcastServerRunning());
    assertFalse(server2.isMaster());
    
    server.stopServer();
    
    Thread.sleep(20000);
    
    assertTrue(server2.isMaster());
    assertTrue(server2.isBroadcastServerRunning());
    
    
    assertFalse(server.isMaster());
    assertFalse(server.isBroadcastServerRunning());
    
    server2.stopServer();
  }
  
   
}
