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
    
    private Logger logger;
    
    //States the master can be in
    enum MasterStates {RUNNING, ELECTED, NOTELECTED};
    @SuppressWarnings("unused") //it is actually used, but is giving a warning
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
		this.logger = LoggerFactory.getLogger(getClass().getSimpleName()) ;
	    logger.info("ZkMaster initialized");
	}
	
	/**
	 * Connects to Zookeeper instance
	 * Timeout is default 15 seconds
	 * @throws IOException
	 */
	public void startZK() throws IOException {
		logger.info("Starting new ZooKeeper connection");
		zk = new ZooKeeper(hostPort, 15000, this);
	}
	
	/**
	 * Closes zookeeper connection
	 * @throws Exception
	 */
	public void stopZK() throws Exception { 
		logger.info("Closing ZooKeeper connection");
		zk.close(); 
	}
	
	/**
	 * Check if ZooKeeper session is active
	 * @return
	 */
	public boolean isConnected() {
		logger.info("Returning connection status: "+this.connected);
        return this.connected;
    }
    
    /**
     * Check if the ZooKeeper session has expired.
     * 
     * @return boolean ZooKeeper session has expired
     */
    public boolean isExpired() {
    	logger.info("Returning expiration status: "+this.expired);
        return this.expired;
    }
    
    /**
     * Check if this node is Master
     * @return
     */
    public boolean isMaster(){
    	logger.info("Returning isMaster status: "+this.isLeader);
    	return this.isLeader;
    }
    
    /**
     * Get the UUID that is the server ID
     * @return the server's ID
     */
    public String getServerId(){
    	logger.info("Returning Server ID: "+this.serverId);
    	return this.serverId;
    }
    
    /**
     * Sets master name
     * @param master String for what the master string will be. Default is "/master". Must begin with "/" character
     * @return true if change was successful, false otherwise
     */
    public boolean setMasterName(String master){
    	logger.info("Setting master name: "+master);
    	if(master.startsWith("/") && master.length()>1){
    		masterName = master;
    		return true;
    	}
    	logger.error(master+" - Master name invalid.  Must start with '/' character");
    	return false;
    }
    
    /**
     * Gets the name that this node will use for master status
     * @return
     */
    String getMasterName(){
    	logger.info("Returning master znode name");
    	return masterName;
    }
	
    /**
     * Asynchronous call
	 * Checks if /master exists
	 * Checks for existence, sets watcher for /master - masterExistsWatcher()
	 * and sets the callback method - masterExistsCallback()
	 */
	void masterExists() {
		logger.info("Starting asynch call to see if master exists");
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
        	logger.info("Starting watcher for master");
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
        	logger.info("Processing Master Exists callback");
            switch (Code.get(rc)) { 
            	//retry on connection loss
	            case CONNECTIONLOSS:
	            	logger.info("Connection lost, Checking if master exists again");
	                masterExists();
	                break;
	            //everything is kosher
	            case OK:
	            	logger.info("Everything ok");
	                break;
	            //try to become master if /master doesn't exist
	            case NONODE:
	            	logger.info("Master doesn't exist, Going to run for master");
	                state = MasterStates.RUNNING;
	                runForMaster();
	                break;
	            //Check data from /master
	            default:     
	            	logger.info("Default case - checking for master");
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
 		logger.info("Checking if master exists");
         zk.getData(masterName, false, masterCheckCallback, null);
     }
 	
    /**
     * Callback for when checkMaster returns
     * Becomes master if possible
     */
    DataCallback masterCheckCallback = new DataCallback() {
        public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        	logger.info("Master Check Callback");
            switch (Code.get(rc)) {
            //Try again
            case CONNECTIONLOSS:
            	logger.info("Connection lost, checking for master again");
                checkMaster();
                break;
            //Try to become master if /master doesn't exist
            case NONODE:
            	logger.info("Master doesn't exist, going to run for master");
                runForMaster();
                break; 
            //Master exists
            case OK:
            	//If this znode is the master then take leadership
                if( serverId.equals( new String(data) ) ) {
                	logger.info("We shall now become master!");
                    state = MasterStates.ELECTED;
                    logger.info("Taking leadership");
                    takeLeadership();
                } 
                //Otherwise don't, but keep checking
                else {
                	logger.info("Checking for master");
                    state = MasterStates.NOTELECTED;
                    masterExists();
                }
                break;
            //Something went wrong
            default:
            	logger.error("Error when reading data");
            	break;
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
		logger.info("Taking leadership");
		isLeader = true;
	}
	
	/**
     * Tries to create a /master lock znode to acquire leadership.
     * /master is ephemeral, so when client disconnects, /master disappears
     * and another node will attempt to become master
     */
    public void runForMaster() {
    	logger.info("Running For Master");
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
        	logger.info("runForMaster Callback");
            switch (Code.get(rc)) { 
            case CONNECTIONLOSS:
            	logger.info("Connection Lost. Checking for master");
            	isLeader = false;
                checkMaster();
                break;
            //We have become master
            case OK:
            	logger.info("We are master");
                state = MasterStates.ELECTED;
                takeLeadership();
                break;
            //Master already exists
            case NODEEXISTS:
            	logger.info("Master already exists");
            	isLeader = false;
            	state = MasterStates.NOTELECTED;
                masterExists();
                break;
            //Something went wrong...
            default:
            	isLeader = false;
            	state = MasterStates.NOTELECTED;
                logger.error("Something went wrong when running for master.") ;
                
            }
        }
    };
}
