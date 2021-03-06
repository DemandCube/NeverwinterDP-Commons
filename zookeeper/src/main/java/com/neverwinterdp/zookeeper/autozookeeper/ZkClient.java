package com.neverwinterdp.zookeeper.autozookeeper;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * Used to perform basic operations on zookeeper
 * All operations are currently done synchronously
 * 
 * Example use:
 * ZkClient c = new ZkClient("127.0.0.1:2181");
 * c.create("/Test", "192.168.1.2");
 * System.out.println(c.getData("/Test"));
 * c.deleteData("/Test");
 * 
 * 
 * @author Richard Duarte
 *
 */
public class ZkClient implements Watcher{
    //ZooKeeper instance
    ZooKeeper zk;
    
    //Form should be [ip address]:[port]
    String hostPort;
    
    //Status booleans
    volatile boolean connected = false;
    volatile boolean expired = false;
    
    private Logger logger;
    
    /**
     * Constructor
     * @param hostPort - format is "[ip/hostname]:[port],[ip/hostname]:[port]..."
     */
    ZkClient(String hostPort) { 
        this.hostPort = hostPort;
        this.logger = LoggerFactory.getLogger(getClass().getSimpleName()) ;
      logger.info("ZkClient initialized");
    }
    
    /**
     * Connect to Zookeeper
     * Default timeout is 15 seconds
     * @throws IOException
     */
    void startZK() throws IOException {
      logger.info("Connecting to: "+this.hostPort);
        zk = new ZooKeeper(this.hostPort, 15000, this);
    }
    
    /**
     * Closes Zookeeper connection
     * @throws InterruptedException
     */
    void stopZK() throws InterruptedException {
      logger.info("Closing connection to zookeeper");
      zk.close();
    }
    
    /**
     * Get if zookeeper is connected
     * @return True if connected, false otherwise
     */
    public boolean isConnected(){
      logger.info("Returning isConnected status: "+this.connected);
      return this.connected;
    }
    
    /**
     * Get if zookeeper session is expired
     * @return true if session is expired, false otherwise
     */
    public boolean isExpired(){
      logger.info("Returning isExpired status: "+this.expired);
      return this.expired;
    }
    
    /**
     * Required method to implement Watcher
     */
  public void process(WatchedEvent e) { 
        System.out.println(e);
        if(e.getType() == Event.EventType.None){
            switch (e.getState()) {
            case SyncConnected:
                connected = true;
                break;
            case Disconnected:
                connected = false;
                break;
            case Expired:
                expired = true;
                connected = false;
                System.out.println("Exiting due to session expiration");
            default:
                break;
            }
        }
    }
  
  /**
   * Synchronous method
   * Create a persistent znode
   * @param znode name of znode, must be valid - i.e. "/master" or "/worker/worker-123"  etc
   * @param data metadata to be associated with znode
   * @return Returns true if function completes successfully
   */
  public boolean create(String znode,String data){
    logger.info("Creating znode: "+znode+":"+data);
    try {
      zk.create(znode, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    } catch (KeeperException | InterruptedException e) {
      logger.error("Exception caught:"+e.getMessage());
      e.printStackTrace();
    } 
    return true;
  }
  
  /**
   * Synchronous method
   * Get data from znode
   * @param znode Name of znode to get data from.  Must be valid - i.e. "/master", "/worker/worker-123", etc
   * @return String representation of data, returns null if znode is invalid
   */
  public String getData(String znode){
    logger.info("Getting data from znode: "+znode);
    Stat stat = new Stat();
    byte data[] = null;
    try {
      data = zk.getData(znode, false, stat);
    } catch (KeeperException | InterruptedException e) {
      logger.error("Exception caught: "+e.getMessage());
      e.printStackTrace();
      return null;
    }
    return new String(data);
  }
  
  /**
   * Synchronous method
   * Deletes znode from zookeeper
   * @param znode Name of znode
   * @return true if deletion was successful, false if failure
   */
  public boolean deleteData(String znode){
    logger.info("Deleting znode: "+znode);
    try {
      zk.delete(znode, -1);
    } catch (InterruptedException | KeeperException e) {
      logger.error("Exception caught: "+e.getMessage());
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
}
