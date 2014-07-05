package com.neverwinterdp.server.shell.js;

import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.gateway.ClusterGateway;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class JSScriptUnitTest {
  static {
    System.setProperty("app.dir", "build/cluster") ;
    System.setProperty("log4j.configuration", "file:src/test/resources/log4j.properties") ;
  }
  
  static protected Server[]      instance ;
  static protected ClusterGateway clusterGateway ;

  @BeforeClass
  static public void setup() throws Exception {
    String[] args = {
      "-Pserver.group=NeverwinterDP", "-Pserver.name=test", "-Pserver.roles=master"
    };
    
    instance = new Server[1] ;
    for(int i = 0; i < instance.length; i++) {
      instance[i] = Server.create(args) ;  
    }
    clusterGateway = new ClusterGateway() ;
  }

  @AfterClass
  static public void teardown() throws Exception {
    clusterGateway.close() ; 
    for(int i = 0; i < instance.length; i++) {
      instance[i].exit(0) ;
    }
  }
  
  @Test
  public void testScriptLib() throws Exception {
    HashMap<String, Object> ctx = new HashMap<String, Object>() ;
    ctx.put("JAVA_CLUSTER_GATEWAY", clusterGateway) ;
    ScriptRunner runner = new ScriptRunner(".", ctx) ;
    runner.require("src/main/javascript/util/io.js");
    runner.require("src/main/javascript/util/assert.js");
    runner.require("src/main/javascript/cluster/cluster.js");
    runner.require("src/main/javascript/cluster/cluster-unit-test.js");
  }
}