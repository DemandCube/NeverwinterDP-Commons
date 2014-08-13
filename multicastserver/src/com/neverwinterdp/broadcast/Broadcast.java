package com.neverwinterdp.broadcast;

import com.neverwinterdp.netty.multicast.MulticastServer;
import com.neverwinterdp.zookeeper.autozookeeper.ZkMaster;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import static org.kohsuke.args4j.ExampleMode.ALL;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;


/**
 * TODO:
 * use logs
 * @author rcduar
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
	
	
	/**
	 * Main method
	 * @param args Command line arguments. Can be found by running "java Broadcast -help"
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		//Parse command line args
		new Broadcast().parseCommandLine(args);
		
		//The map of data to pass into the broadcast server
		Map<String,String> zkConnectionMap = new HashMap<String,String>();
		
		//Get list of zookeepers IP:port from properties file
		zkConnectionMap = readPropertiesFile(propFile);
		
		//If "broadcast" key doesn't exist and no zookeeper IP:Port was passed via the command line, exit out
		if(broadcastZookeeper == null && !zkConnectionMap.containsKey("broadcast")){
			System.err.println(propFile+": Does not contain \"broadcast\" key!\nThis is required if the -broadcastZookeeper option is omitted.\nThis is the zookeeper instance upon which Broadcast will connect to");
			System.exit(-1);
		}
		
		//Ensure all of them match [IP/Hostname]:[Port] format
		for (String zk : zkConnectionMap.values()) {
			if(!verifyHostPortFormat(zk)){
				System.err.println("Bad formatting: "+ zk);
				System.exit(-1);
			}
		}
		//If zookeeper to connect to is on the command line, verify its ok
		if(broadcastZookeeper != null && !verifyHostPortFormat(broadcastZookeeper)){
			System.err.println("Bad formatting: "+ broadcastZookeeper);
			System.exit(-1);
		}
		
		//RUN FOREVER
		while(true){
			//Connect to zookeeper either by -broadcastZookeeper command line argument
			// or by "broadcast" key in broadcast.properties file
			ZkMaster m=null;
			if(broadcastZookeeper != null){
				m = new ZkMaster(broadcastZookeeper);
			}
			else{
				 m = new ZkMaster(zkConnectionMap.get("broadcast"));
			}
			
			//Set the /master keyword and connect to ZooKeeper
			m.setMasterName("/masterBroadcaster");
			m.startZK();
			
			//Loop until connected
			while(!m.isConnected()){
				System.out.println("Not connected...");
	            Thread.sleep(1000);
	        }
			
			//Try to become master
			System.out.println("Connected!");
			m.runForMaster();
			
			//runForMaster is asynchronous, so go ahead and take a break
			Thread.sleep(3000);
			
			//If we're master, go ahead and start the broadcast server
			if(m.isMaster()){
				System.out.println("We are master! Starting broadcast server");
				MulticastServer broadcaster = new MulticastServer(udpPort, zkConnectionMap);
				broadcaster.run();
		        
				//We shouldn't ever hit this step - assuming nothing goes wrong...
				broadcaster.stop();
			}
			else{
				System.out.println("We ain't master!");
				Thread.sleep(10000);
			}
			
			while(!m.isExpired()){
	            Thread.sleep(1000);
	        }
			
			System.out.println("Expired");
	        m.stopZK();
	        System.out.println("Exited");
		}
	}
	
	/**
	 * Verifies the list of hostnames and ports are in the correct format
	 * Format should be "[hostname/IP]:[Port]" or "[hostname/IP]:[Port],[hostname2/IP2]:[Port2], ...."
	 * @param hostlist The string to verify
	 * @return True if the format is acceptable, false otherwise
	 */
	public static boolean verifyHostPortFormat(String hostlist){
		String[] zks = hostlist.split(",");
		boolean retVal = true;
		for(int i=0; i<zks.length; i++){
			if(!zks[i].matches("(([\\d]+\\.){3}[\\d]+|\\S+):[\\d]+")){
				System.err.println(zks[i]+" is invalid.  Must be [IP/hostname]:[port]");
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
			ex.printStackTrace();
			System.exit(-1);
		}
		//Close the file
		finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
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
		
		try {
            // parse the arguments.
            parser.parseArgument(args);
        } catch( CmdLineException e ) {
        	//Catch any invalid arguments
            System.err.println(e.getMessage());
            printUsage(System.err,parser);
        }
		//If -help was on command line,
		//Print out help message and exit
		if (help == true){
			printUsage(System.out,parser);
			System.exit(0);
		}
	}
	
	
	/**
	 * Print usage to appropriate output
	 * @param output Either System.out, System.err
	 * @param parser the CmdLineParser object
	 */
	public void printUsage(PrintStream output, CmdLineParser parser){
		// print option sample. This is useful some time
		output.println("Example: java Broadcast "+parser.printExample(ALL));
		parser.printUsage(output);
	}
	
}
