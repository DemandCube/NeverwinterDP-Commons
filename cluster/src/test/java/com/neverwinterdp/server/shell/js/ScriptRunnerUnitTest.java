package com.neverwinterdp.server.shell.js;

import java.util.HashMap;

import org.junit.Test;

public class ScriptRunnerUnitTest {
  
  @Test
  public void testScriptRunner() throws Exception {
    HashMap<String, Object> ctx = new HashMap<String, Object>() ;
    ctx.put("hello", "Hello") ;
    ScriptRunner runner = new ScriptRunner(".", ctx) ;
    runner.eval("print('ctx hello = ' +  hello + '\\n')") ;
    runner.eval("print('ctx hello = ' +  JSON.stringify({name: 'Tuan'}))") ;
  }
  
  @Test
  public void testConsole() throws Exception {
    HashMap<String, Object> ctx = new HashMap<String, Object>() ;
    ScriptRunner runner = new ScriptRunner(".", ctx) ;
    runner.require("src/main/resources/js/io.js");
    runner.require("src/main/resources/js/io-unit-test.js");
  }
}
