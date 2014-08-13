package com.neverwinterdp.broadcast;

import com.neverwinterdp.zookeeper.autozookeeper.ZkMaster;

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
 * broadcast the zookeeper location
 * use logs
 * @author rcduar
 *
 */
public class Broadcast {
	
	//Command line args
	@Option(name="-propertiesFile",usage="Java properties file")
	public static String propFile = "broadcast.properties";
	
	@Option(name="-zookeepers",usage="List of zookeepers.  Format - [ip address]:[port],[ip address2]:[port2],...")
	public static String zkList = null;
	
	@Option(name="-help", usage="Displays help message")
	public static boolean help = false;
	
	/**
	 * Main method
	 * @param args Command line arguments. Can be found by running "java Broadcast -help"
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		//Parse command line args
		new Broadcast().parseCommandLine(args);
		
		//The string we'll pass to connect to a zookeeper
		String zkConnection = "";
		
		//Get list of zookeepers IP:port from command line
		//Ensure all of them match [IP/Hostname]:[Port] format
		if(zkList != null){
			if(verifyHostPortFormat(zkList)){
				zkConnection = zkList;
			}
			else{
				System.err.println("Bad formatting: "+ zkList);
				System.exit(-1);
			}
		}
		//Else read in from propertiesFile
		else{
			zkConnection = readPropertiesFile(propFile);
			if(!verifyHostPortFormat(zkConnection)){
				System.err.println("Bad formatting: "+ zkConnection);
				System.exit(-1);
			}
		}
		
		
		ZkMaster m = new ZkMaster(zkConnection);
		
		m.setMasterName("/masterBroadcaster");
		m.startZK();
		
		System.out.println("Started");
		
		while(!m.isConnected()){
			System.out.println("Not connected");
            Thread.sleep(100);
        }
		
		System.out.println("Connected");
		m.runForMaster();
		
		if(m.isMaster()){
			System.out.println("We are master!");
		}
		else{
			System.out.println("We ain't master!");
		}
		
		
		Thread.sleep(5000);
		
		if(m.isMaster()){
			System.out.println("We are master!");
		}
		else{
			System.out.println("We ain't master!");
		}
		
		System.out.println("Master was run for");
		
		while(!m.isExpired()){
            Thread.sleep(1000);
        }
		
		System.out.println("Expired");
        m.stopZK();
        System.out.println("Exited");
	}
	
	/**
	 * Verifies the list of hostnames and ports are in the correct format
	 * @param hostlist The string to verify
	 * @return True if the format is acceptable, false otherwise
	 */
	public static boolean verifyHostPortFormat(String hostlist){
		String[] zks = hostlist.split(",");
		for(int i=0; i<zks.length; i++){
			if(!zks[i].matches("(([\\d]+\\.){3}[\\d]+|\\S+):[\\d]+")){
				System.err.println(zks[i]+" is invalid.  Must be [IP/hostname]:[port]");
				return false;
			}
		}
		return true;
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
            System.err.println(e.getMessage());
            printUsage(System.err,parser);
        }
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
		// print option sample. This is useful some time
		output.println("Example: java Broadcast "+parser.printExample(ALL));
		parser.printUsage(output);
	}
	
	/**
	 * Read in properties file containing zookeeper hostnames and port numbers
	 * @param filename Path to file to read in
	 * @return String of list of [hostnames/IPs]:[Port].  Format is "[ip/hostname]:[port],[ip/hostname]:[port]..." 
	 */
	public static String readPropertiesFile(String filename){
		Properties prop = new Properties();
		InputStream input = null;
		String retVal= null;
		try {
			input = new FileInputStream(filename);
	 
			// load a properties file
			prop.load(input);
			// get the property value and print it out
			retVal = prop.getProperty("zookeepers");
			
		} 
		catch (IOException ex) {
			ex.printStackTrace();
		} 
		finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return retVal;
	}
}
