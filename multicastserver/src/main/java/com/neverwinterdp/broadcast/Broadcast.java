package com.neverwinterdp.broadcast;

import static org.kohsuke.args4j.ExampleMode.ALL;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.netty.multicast.MulticastServer;
import com.neverwinterdp.zookeeper.autozookeeper.ZkMaster;

/**
 * 
 * @author Richard Duarte
 *
 */
public class Broadcast {
  //////////////////////////////////////////////////////////////////////////////////////
  //Command line args
  @Option(name="-propertiesFile",usage="Java properties file")
  public static String propFile = "broadcast.properties";
  
  @Option(name="-broadcastZookeeper",usage="The zookeeper [host]:[port] for this server to connect to")
  public static String broadcastZookeeper=null;
  
  @Option(name="-udpPort", usage="UDP port to run Broadcast server on")
  public static int udpPort = 1111;
  
  @Option(name="-help", usage="Displays help message")
  public static boolean help = false;
  //////////////////////////////////////////////////////////////////////////////////////
  
  
  //The map of data to pass into the broadcast server
  private Map<String,String> zkConnectionMap = new HashMap<String,String>();
  private static Logger logger;
  //ZkMaster object
  private ZkMaster m=null;
  //[Host]:[Port] of zookeeper Broadcast will connect to
  private String myZookeeperConnection;
  //The master string to use for zookeeper
  private String masterName="/masterBroadcaster";
  //UDP broadcast server object
  private MulticastServer broadcaster=null;
  //Flag for whether broadcast server is running or not
  private boolean serverRunning=false;
  
  /**
   * Constructor.  Meant to parse out command line arguments
   * @param args
   */
  public Broadcast(String args[]){
    logger = LoggerFactory.getLogger("Broadcast");
    logger.info("Initializing Broadcast object");
    for(int i=0; i<args.length; i++){
      logger.info("Args passed in:"+args[i]);
    }
    this.parseCommandLine(args);
  }
  
  /**
   * See if this object is the master
   * @return whether ZkMaster m is master or not
   */
  public boolean isMaster(){
    if(this.m==null){
      return false;
    }
    return this.m.isMaster();
  }
  
  /**
   * Get server ID
   * @return UUID that is the server ID
   */
  public String getServerID(){
    return this.m.getServerId();
  }
  
  /**
   * See if this object is running broadcast server
   * @return boolean - true if broadcast is running, false otherwise
   */
  public boolean isBroadcastServerRunning(){
    return this.serverRunning;
  }
  
  /**
   * Returns true if help flag has been set on command line
   * @return true if help flag has been set, otherwise false
   */
  public boolean getHelp(){
    return Broadcast.help;
  }
  
  /**
   * Read in the properties file, verify all the data is appropriate
   * @return
   */
  public boolean initialize(){
    logger.info("Reading in propery file: "+propFile);
    //Get list of zookeepers IP:port from properties file
    zkConnectionMap = readPropertiesFile(propFile);
    
    logger.info("Connection Map: "+zkConnectionMap.toString());
    
    //If "broadcast" key doesn't exist and no zookeeper IP:Port was passed via the command line
    if(broadcastZookeeper == null && !zkConnectionMap.containsKey("broadcast")){
      logger.error(propFile+": Does not contain \"broadcast\" key!\nThis is required if the -broadcastZookeeper option is omitted.\nThis is the zookeeper instance upon which Broadcast will connect to");
      return false;
    }
    
    //Ensure all of them match [IP/Hostname]:[Port] format
    for (String zk : zkConnectionMap.values()) {
      if(!verifyHostPortFormat(zk)){
        logger.error("Bad formatting: "+ zk);
        return false;
      }
    }
    //If zookeeper to connect to is on the command line, verify its ok
    if(broadcastZookeeper != null && !verifyHostPortFormat(broadcastZookeeper)){
      logger.error("Bad formatting: "+ broadcastZookeeper);
      return false;
    }
    
    //Set where Broadcast will connect to
    //Connect to zookeeper either by -broadcastZookeeper command line argument
    // or by "broadcast" key in broadcast.properties file
    if(broadcastZookeeper != null){
      myZookeeperConnection = broadcastZookeeper;
    }
    else{
      myZookeeperConnection = zkConnectionMap.get("broadcast");
    }
    logger.info("Zookeeper will connect to :"+myZookeeperConnection);
    
    return true;
    
  }
  
  /**
   * Main server loop.  Should run forever
   * Attempts to become zookeeper master
   * If master, then launches broadcast server
   * @throws Exception
   */
  public void runServerLoop() throws Exception{
    logger.info("Entering infinite loop");
    this.m = new ZkMaster(myZookeeperConnection);
    //Set the /master keyword and connect to ZooKeeper
    logger.info("Setting master name to "+this.masterName);
    this.m.setMasterName(this.masterName);
    
    
    //RUN FOREVER
    while(true){
      logger.info("Connecting to zookeeper at: "+myZookeeperConnection);
      this.m.startZK();
      //Loop until connected
      while(!m.isConnected()){
        logger.info("Not connected...");
        Thread.sleep(1000);
      }
      
      //Try to become master
      logger.info("Connected! Will now attempt to run for master");
      this.m.runForMaster();
      
      //runForMaster is asynchronous, so go ahead and take a break
      Thread.sleep(3000);
      
      //If we're master, go ahead and start the broadcast server
      if(this.m.isMaster()){
        logger.info("We are master! Starting broadcast server");
        this.broadcaster = new MulticastServer(udpPort, zkConnectionMap);
        this.serverRunning = true;
        this.broadcaster.run();
        
        logger.info("Broadcast server has stopped!");
        //We shouldn't ever hit this step since run() blocks
        //That's assuming nothing goes wrong though
        this.broadcaster.stop();
        this.serverRunning = false;
      }
      else{
        this.serverRunning = false;
        logger.info("We ain't master!");
        Thread.sleep(10000);
      }
      
      logger.info("Disconnecting zookeeper");
      this.serverRunning = false;
      this.m.stopZK();
    }
  }
  
  /**
   * Stops the broadcaster UDP server and closes connection to Zookeeper
   */
  public void stopServer() {
    try {
      this.m.stopZK();
    } 
    catch (Exception e) {
      logger.error(e.getMessage());
    }
    if(this.serverRunning){
      this.serverRunning=false;
      this.broadcaster.stop();
    }
    
    
    this.serverRunning=false;
  }
  
  
  
  /**
   * Verifies the list of hostnames and ports are in the correct format
   * Format should be "[hostname/IP]:[Port]" or "[hostname/IP]:[Port],[hostname2/IP2]:[Port2], ...."
   * @param hostlist The string to verify
   * @return True if the format is acceptable, false otherwise
   */
  public static boolean verifyHostPortFormat(String hostlist){
    logger.info("Verifying HostPort format");
    String[] zks = hostlist.split(",");
    boolean retVal = true;
    for(int i=0; i<zks.length; i++){
      if(!zks[i].matches("(([\\d]+\\.){3}[\\d]+|\\S+):[\\d]+")){
        logger.error(zks[i]+" is invalid.  Must be [IP/hostname]:[port]");
        retVal=false;
      }
    }
    return retVal;
  }
  
  
  
  /**
   * Read in properties file containing zookeeper hostnames and port numbers
   * @param filename Path to file to read in
   * @return String of list of [hostnames/IPs]:[Port].  Format is "[ip/hostname]:[port],[ip/hostname]:[port]..." 
   */
  public static Map<String,String> readPropertiesFile(String filename){
    logger.info("Reading in property file");
    Properties prop = new Properties();
    InputStream input = null;
    Map<String, String> map = new HashMap<String, String>();
    
    try {
      // load a properties file
      input = new FileInputStream(filename);
      prop.load(input);
      
      //Load property file contents into a hashmap
      for (final String name: prop.stringPropertyNames()){
          map.put(name, prop.getProperty(name));
      }  
    } 
    //Likely we can't read the properties file or something
    catch (IOException ex) {
      logger.error(ex.getMessage());
    }
    //Close the file
    finally {
      if (input != null) {
        try {
          input.close();
        } 
        catch (IOException e) {
          logger.error(e.getMessage());
        }
      }
    }
    
    //return hashmap
    return map;
  }
  
  
  
  /**
   * Parse out arguments
   * @param args command line arguments from main()
   */
  public void parseCommandLine(String[] args){
    CmdLineParser parser = new CmdLineParser(this);
    
    try{
      // parse the arguments.
      parser.parseArgument(args);
    } 
    catch( CmdLineException e ) {
      //Catch any invalid arguments
      System.err.println(e.getMessage());
      printUsage(System.err,parser);
    }
    //If -help was on command line,
    //Print out help message
    if (help == true){
      printUsage(System.out,parser);
    }
  }
  
  
  /**
   * Print usage to appropriate output
   * @param output Either System.out, System.err
   * @param parser the CmdLineParser object
   */
  public void printUsage(PrintStream output, CmdLineParser parser){
    output.println("Example: java Broadcast "+parser.printExample(ALL));
    parser.printUsage(output);
  }
  
  
  /**
   * Main method
   * @param args Command line arguments. Can be found by running "java Broadcast -help"
   * @throws Exception
   */
  public static void main(String args[]) throws Exception {
    Broadcast b = new Broadcast(args);
    
    //If initialize() returns false,
    //Then something was wrong with the configuration
    if(!b.initialize()){
      System.exit(-1);
    }
    
    while(true){
      try{
        b.runServerLoop();
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }
  }
}
