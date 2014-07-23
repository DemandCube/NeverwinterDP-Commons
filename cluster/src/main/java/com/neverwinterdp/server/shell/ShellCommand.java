package com.neverwinterdp.server.shell;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.neverwinterdp.server.gateway.Command;

public class ShellCommand {
  private String name ;
  private Map<String, Class<? extends ShellSubCommand>> subCommands = new HashMap<String, Class<? extends ShellSubCommand>>() ;
  
  public ShellCommand() {
  }
  
  public ShellCommand(String name) {
    this.name = name ;
  }
  
  public String getName() { return this.name ; }
  public void setName(String name) { this.name = name ; }
  
  public <T extends ShellSubCommand> void add(String name, Class<T> type) {
    subCommands.put(name, type) ;
  }
  
  public boolean hasCommand(String name) { return subCommands.containsKey(name) ; }
  
  public void execute(ShellContext ctx, Command command) throws Exception {
    if("help".equalsIgnoreCase(command.getSubCommand())) {
      help() ;
      return ;
    }
    Class<ShellSubCommand> clazz = (Class<ShellSubCommand>) subCommands.get(command.getSubCommand()) ;
    if(clazz != null) {
      ShellSubCommand subCommand = clazz.newInstance() ;
      ctx.onStartCommand(this, subCommand);
      try {
        subCommand.execute(ctx, command);
      } catch(Throwable t) {
        ctx.getExecuteContext().setError(t);
        StringWriter writer = new StringWriter() ;
        writer.append(command.getCommandLine()).append("\n") ;
        t.printStackTrace(new PrintWriter(writer));
        ctx.console().println(writer.getBuffer().toString());
      }
      ctx.onFinishCommand(this, subCommand);
    }
  }
  
  public void help() {
  }
  
  static public ShellCommand[] loadByAnnotation(String ... packages) {
    List<ShellCommand> holder = new ArrayList<ShellCommand>() ;
    for(String pkg : packages) {
      Reflections reflections = new Reflections(pkg);
      Set<Class<?>> annotateds = reflections.getTypesAnnotatedWith(ShellCommandConfig.class);
      for(Class<?> clazz : annotateds) {
        try {
          ShellCommand cgroup  = (ShellCommand) clazz.newInstance();
          ShellCommandConfig config = clazz.getAnnotation(ShellCommandConfig.class) ;
          cgroup.setName(config.name());
          holder.add(cgroup) ;
        } catch (InstantiationException | IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
    return holder.toArray(new ShellCommand[holder.size()]) ;
  }
}