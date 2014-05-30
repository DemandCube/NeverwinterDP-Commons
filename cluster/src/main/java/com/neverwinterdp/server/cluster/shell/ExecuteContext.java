package com.neverwinterdp.server.cluster.shell;

import java.util.HashMap;
import java.util.Map;


public class ExecuteContext {
  private CommandGroup        group;
  private Command             command;
  private String[]            args;
  private String              consoleOutput;
  private Throwable           error ;
  private Map<String, Object> data = new HashMap<String, Object>();
  
  public CommandGroup getGroup() { return group; }
  public void setGroup(CommandGroup group) { this.group = group ; }

  public Command getCommand() {
    return command;
  }

  public void setCommand(Command command) {
    this.command = command;
  }

  public String[] getArgs() {
    return args;
  }

  public void setArgs(String[] args) {
    this.args = args;
  }

  public String getConsoleOutput() {
    return consoleOutput;
  }

  public void setConsoleOutput(String consoleOutput) {
    this.consoleOutput = consoleOutput;
  }
  
  public boolean hasError() { return error != null; }
  
  public Throwable getError() { return this.error ; }
  public void setError(Throwable t) { this.error = t ; }
}
