package com.neverwinterdp.zookeeper.autozookeeper;


import java.io.IOException;
import java.util.UUID;

import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Class to manage Master Zookeeper node
 * Automatically becomes Master, and upon failure another znode should grab the master role
 * 
 * Example usage:
 * ZkMaster m = new ZkMaster("127.0.0.1:2181");
 *  
 * //optional, just if you want to have a custom znode name instead of /master
 * m.setMasterName("/masterBroadcaster"); 
 * 
 * //Connect to zookeeper
 * m.startZK();
 * while(!m.isConnected()){
 * 		//Not connected, so wait
 * 		Thread.sleep(100);
 * }
 * 
 * //Now that we're connected, try to become master
 * m.runForMaster();
 * 
 * //runForMasteR() is an asynchronous call, 
 * //so it will return before we actually know if we're master or not
 * //so a little sleep here will help
 * Thread.sleep(5000);
 * 
 * if(m.isMaster()){
 * 		System.out.println("We are master!");
 * 		//do what master should do
 * 	}
 *  else{
 *  	System.out.println("We ain't master!");
 *  }
 *  
 *  //While our session doesn't expire, just hang out
 *  while(!m.isExpired()){
 *  	Thread.sleep(1000);
 *  }	
 *  
 *  //Close connection to zookeeper
 *  m.stopZK();
 * 
 * @author Richard Duarte
 *
 */
public class ZkMaster implements Watcher{
	//Unique ID for this instance
	private String serverId = UUID.randomUUID().toString();
	
	//Determine if this object is the leader
	boolean isLeader = false;
    
	//Zookeeper object
	ZooKeeper zk;
	
	//Form should be [ip address]:[port]
    String hostPort;
    
    //States the master can be in
    enum MasterStates {RUNNING, ELECTED, NOTELECTED};
    private volatile MasterStates state = MasterStates.RUNNING;
    
    //Status booleans
    private volatile boolean connected = false;
    private volatile boolean expired = false;
    
    private String masterName = "/master";
    
    /**
     * Constructor
     * @param hostPort - format is "[ip/hostname]:[port],[ip/hostname]:[port]..."
     */
	public ZkMaster(String hostPort) { 
		this.hostPort = hostPort;
	}
	
	/**
	 * Connects to Zookeeper instance
	 * Timeout is default 15 seconds
	 * @throws IOException
	 */
	public void startZK() throws IOException {
		zk = new ZooKeeper(hostPort, 15000, this);
	}
	
	/**
	 * Closes zookeeper connection
	 * @throws Exception
	 */
	public void stopZK() throws Exception { 
		zk.close(); 
	}
	
	/**
	 * Check if ZooKeeper session is active
	 * @return
	 */
	public boolean isConnected() {
        return connected;
    }
    
    /**
     * Check if the ZooKeeper session has expired.
     * 
     * @return boolean ZooKeeper session has expired
     */
    public boolean isExpired() {
        return expired;
    }
    
    /**
     * Check if this node is Master
     * @return
     */
    public boolean isMaster(){
    	return isLeader;
    }
    
    /**
     * Get the UUID that is the server ID
     * @return the server's ID
     */
    public String getServerId(){
    	return this.serverId;
    }
    
    /**
     * Sets master name
     * @param master String for what the master string will be. Default is "/master". Must begin with "/" character
     * @return true if change was successful, false otherwise
     */
    public boolean setMasterName(String master){
    	if(master.startsWith("/") && master.length()>1){
    		masterName = master;
    		return true;
    	}
    	return false;
    }
    
    /**
     * Gets the name that this node will use for master status
     * @return
     */
    String getMasterName(){
    	return masterName;
    }
	
    /**
     * Asynchronous call
	 * Checks if /master exists
	 * Checks for existence, sets watcher for /master - masterExistsWatcher()
	 * and sets the callback method - masterExistsCallback()
	 */
	void masterExists() {
        zk.exists(masterName, 
                masterExistsWatcher, 
                masterExistsCallback, 
                null);
    }
	
	/**
     * Watches if /master is deleted, tries to become master if it is
     */
    Watcher masterExistsWatcher = new Watcher(){
        public void process(WatchedEvent e) {
            if(e.getType() == EventType.NodeDeleted) {
                if(masterName.equals( e.getPath())){
                	runForMaster();
                }
            }
        }
    };
	
    /**
     * Callback for when masterExists returns
     */
    StatCallback masterExistsCallback = new StatCallback() {
        public void processResult(int rc, String path, Object ctx, Stat stat){
            switch (Code.get(rc)) { 
            	//retry on connection loss
	            case CONNECTIONLOSS:
	                masterExists();
	                break;
	            //everything is kosher
	            case OK:
	                break;
	            //try to become master if /master doesn't exist
	            case NONODE:
	                state = MasterStates.RUNNING;
	                runForMaster();
	                break;
	            //Check data from /master
	            default:     
	                checkMaster();
	                break;
            }
        }
    };
    
    /**
     * Asynchronous call
     * Checks data from /master
     * Sets callback - masterCheckCallback()
     */
 	void checkMaster() {
         zk.getData(masterName, false, masterCheckCallback, null);
     }
 	
    /**
     * Callback for when checkMaster returns
     * Becomes master if possible
     */
    DataCallback masterCheckCallback = new DataCallback() {
        public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
            switch (Code.get(rc)) {
            //Try again
            case CONNECTIONLOSS:
                checkMaster();
                break;
            //Try to become master if /master doesn't exist
            case NONODE:
                runForMaster();
                break; 
            //Master exists
            case OK:
            	//If this znode is the master then take leadership
                if( serverId.equals( new String(data) ) ) {
                    state = MasterStates.ELECTED;
                    takeLeadership();
                } 
                //Otherwise don't, but keep checking
                else {
                    state = MasterStates.NOTELECTED;
                    masterExists();
                }
                break;
            //Something went wrong
            default:
            	break;
                //LOG.error("Error when reading data.", 
                //        KeeperException.create(Code.get(rc), path));               
            }
        } 
    };
    
    
	/**
     * This method implements the process method of the
     * Watcher interface. We use it to deal with the
     * different states of a session. 
     * 
     * @param e new session event to be processed
     */
    public void process(WatchedEvent e) {  
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
            default:
                break;
            }
        }
    }
	
	
	/**
	 * This is where we would perform recovery to recover abandoned tasks
	 * But that shit's complicated
	 */
	void takeLeadership(){
		isLeader = true;
	}
	
	/**
     * Tries to create a /master lock znode to acquire leadership.
     * /master is ephemeral, so when client disconnects, /master disappears
     * and another node will attempt to become master
     */
    public void runForMaster() {
        zk.create(masterName, 
                serverId.getBytes(), 
                Ids.OPEN_ACL_UNSAFE, 
                CreateMode.EPHEMERAL,
                masterCreateCallback,
                null);
    }
	
	/**
	 * Callback for when runForMaster() returns
	 * Sets state and tries to take leadership if need be
	 */
	StringCallback masterCreateCallback = new StringCallback() {
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (Code.get(rc)) { 
            case CONNECTIONLOSS:
            	isLeader = false;
                checkMaster();
                break;
            //We have become master
            case OK:
                state = MasterStates.ELECTED;
                takeLeadership();
                break;
            //Master already exists
            case NODEEXISTS:
            	isLeader = false;
            	state = MasterStates.NOTELECTED;
                masterExists();
                break;
            //Something went wrong...
            default:
            	isLeader = false;
            	state = MasterStates.NOTELECTED;
                //LOG.error("Something went wrong when running for master.", 
                //        KeeperException.create(Code.get(rc), path));
            }
            //LOG.info("I'm " + (state == MasterStates.ELECTED ? "" : "not ") + "the leader " + serverId);
        }
    };
}
