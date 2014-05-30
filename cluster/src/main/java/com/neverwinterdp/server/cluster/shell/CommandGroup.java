package com.neverwinterdp.server.cluster.shell;

import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.JCommander;

public class CommandGroup {
  private String name ;
  private Map<String, Class<? extends Command>> commands = new HashMap<String, Class<? extends Command>>() ;
  
  public CommandGroup(String name) {
    this.name = name ;
  }
  
  public String getName() { return this.name ; }
  
  public <T extends Command> void add(String name, Class<T> type) {
    commands.put(name, type) ;
  }
  
  public boolean hasCommand(String name) {
    return commands.containsKey(name) ;
  }
  public void execute(ShellContext ctx, String[] args) throws Exception {
    String commandName = args[0] ;
    if("help".equalsIgnoreCase(commandName)) {
      help() ;
      return ;
    }
    Class<Command> clazz = (Class<Command>) commands.get(commandName) ;
    if(clazz != null) {
      Command command = clazz.newInstance() ;
      ctx.onStartCommand(this, command, args);
      String[] newargs = new String[args.length - 1] ;
      System.arraycopy(args, 1, newargs, 0, newargs.length);
      try {
        new JCommander(command, newargs) ;
        command.execute(ctx);
      } catch(Throwable t) {
        ctx.getExecuteContext().setError(t);
      }
      ctx.onFinishCommand(this, command);
    }
  }
  
  public void help() {
  }
}
