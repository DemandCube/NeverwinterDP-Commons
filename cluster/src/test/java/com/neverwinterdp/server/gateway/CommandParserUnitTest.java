package com.neverwinterdp.server.gateway;

import static org.junit.Assert.*;

import org.junit.Test;

public class CommandParserUnitTest {
  @Test
  public void test() {
    String cmdLine = "server ping --member-name generic --unkown \"unknown command string\" mainParam" ;
    Command parser = new Command(cmdLine) ;
    assertEquals("server", parser.getCommand()) ;
    assertEquals("ping", parser.getSubCommand()) ;
    assertEquals("generic", parser.getMemberSelector().memberName) ;
    
    
    String string2 = "echo \"line exp1 exp2 2 --line exp3 exp4 2\"" ;
    parser = new Command(string2, false) ;
  }
}