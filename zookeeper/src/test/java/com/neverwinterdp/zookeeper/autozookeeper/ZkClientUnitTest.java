package com.neverwinterdp.zookeeper.autozookeeper;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.zookeeper.cluster.ZookeeperClusterBuilder;
import com.neverwinterdp.zookeeper.autozookeeper.ZkClient;

public class ZkClientUnitTest {
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
     * Test ability to create, retrieve and delete data
     * @throws Exception
     */
    @Test
    public void testZkClientCreateGetAndDelete() throws Exception {
      ZkClient c = new ZkClient(connection);
      
      String znodename = "/theznode";
      String data = "Goobleygoop";
      
      c.startZK();
      
      while(!c.isConnected()){
        Thread.sleep(100);
      }
      
      assertTrue(c.create(znodename, data));
      assertEquals(c.getData(znodename),data);
      assertTrue(c.deleteData(znodename));
      
      c.stopZK();
    }
    
    /**
     * Test that null is returned when getting invalid znode's data
     * @throws Exception
     */
    @Test
    public void testZkMasterGetInvalidZnode() throws Exception {
      ZkClient c = new ZkClient(connection);
      
      String znodename = "/doesNotExist";
      c.startZK();
      
      while(!c.isConnected()){
        Thread.sleep(100);
      }
      
      //should return null for znode that doesn't exist
      assertNull(c.getData(znodename));
      c.stopZK();
    }
    
    /**
     * Test that trying to delete an invalid znode returns false
     * @throws Exception
     */
    @Test
    public void testZkMasterDeleteInvalidZnode() throws Exception {
      ZkClient c = new ZkClient(connection);
      
      String znodename = "/doesNotExist";
      c.startZK();
      
      while(!c.isConnected()){
        Thread.sleep(100);
      }
      
      assertFalse(c.deleteData(znodename));
      
      c.stopZK();
    }
}
