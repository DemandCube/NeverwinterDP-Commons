package com.neverwinterdp.server;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.neverwinterdp.util.IOUtil;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class Main {
  static public class Options {
    @Parameter(names = "-config", description = "The configuration file in the properties format")
    String configFile;
  }

  
  static public void main(String[] args) throws Exception {
    if(args == null || args.length == 0) {
      args = new String[] {
        "-config", "classpath:server-default-configuration.json"
      };
    }
    Options options = new Options();
    new JCommander(options, args);
    String propsConfig = IOUtil.getResourceAsString(options.configFile, "UTF-8") ;
    
    Thread.currentThread().join();
  }
}
