package com.neverwinterdp.zookeeper.autozookeeper;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.zookeeper.autozookeeper.ZkMaster;
import com.neverwinterdp.zookeeper.autozookeeper.ZkClient;
import com.neverwinterdp.zookeeper.cluster.ZookeeperClusterBuilder;

/**
 * @author Richard Duarte
 */
public class ZkMasterTest {
  static ZookeeperClusterBuilder clusterBuilder ;
  static String connection;

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
  }

  /**
   * Destroy zookeeper server
   * @throws Exception
   */
  @AfterClass
  static public void teardown() throws Exception {
    clusterBuilder.destroy();
  }
  
  /**
   * Make sure we can connect to zookeeper
   * When running for master, assert we are master
   * @throws Exception
   */
  @Test
  public void testZkMasterSimple() throws Exception {
	  ZkMaster m = new ZkMaster(connection);
	  
	  m.startZK();
	  while(!m.isConnected()){
		  Thread.sleep(100);
	  }
	  m.runForMaster();
	  Thread.sleep(5000);
	  assertTrue(m.isMaster());
	  
	  m.stopZK();
	  
	  //Make sure we time out the zookeeper connection before starting next test
	  Thread.sleep(15000);
  }
  
  
  /**
   * Make sure that when we run two ZkMaster's, only one is master
   * and when the master disconnects, the 2nd becomes master
   * @throws Exception
   */
  @Test
  public void testTwoZkMaster() throws Exception {
	  ZkMaster m = new ZkMaster(connection);
	  ZkMaster m2 = new ZkMaster(connection);
	  
	  m.startZK();
	  m2.startZK();
	  
	  while(!m.isConnected() && !m2.isConnected()){
		  Thread.sleep(100);
	  }
	  
	  //Lets make sure m is master
	  m.runForMaster();
	  Thread.sleep(5000);
	  //m2 should be unable to become master
	  m2.runForMaster();
	  
	  //Assert the things that should be
	  assertTrue(m.isMaster());
	  assertFalse(m2.isMaster());
	  
	  //Disconnect m
	  m.stopZK();
	  
	  //Wait a bit for m2 to catch up
	  Thread.sleep(5000);
	  //Make sure m2 is master now
	  assertTrue(m2.isMaster());
	  
	  Thread.sleep(15000);
  }
  
  /**
   * Make sure we can connect to zookeeper
   * When running for master, assert we are master
   * @throws Exception
   */
  @Test
  public void testZkMasterSetMasterName() throws Exception {
	  ZkMaster m = new ZkMaster(connection);
	  ZkClient c = new ZkClient(connection);
	 
	  String mastername="/masterBroadcaster";
	  
	  m.setMasterName(mastername);
	  
	  c.startZK();
	  m.startZK();
	  
	  while(!m.isConnected() && !c.isConnected()){
		  Thread.sleep(100);
	  }
	  m.runForMaster();
	  Thread.sleep(5000);
	  assertTrue(m.isMaster());
	  
	  assertEquals(m.getServerId(),c.getData(mastername));
	  
	  
	  m.stopZK();
	  c.stopZK();
	  
	  //Make sure we time out the zookeeper connection before starting next test
	  Thread.sleep(15000);
  }
}
