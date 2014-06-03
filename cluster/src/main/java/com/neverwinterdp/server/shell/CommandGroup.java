package com.neverwinterdp.server.shell;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.beust.jcommander.JCommander;
import com.neverwinterdp.util.text.StringUtil;

public class CommandGroup {
  private String name ;
  private Map<String, Class<? extends Command>> commands = new HashMap<String, Class<? extends Command>>() ;
  
  public CommandGroup() {
  }
  
  public CommandGroup(String name) {
    this.name = name ;
  }
  
  public String getName() { return this.name ; }
  public void setName(String name) { this.name = name ; }
  
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
        StringWriter writer = new StringWriter() ;
        writer.append(StringUtil.join(args, " ")).append("\n") ;
        t.printStackTrace(new PrintWriter(writer));
        ctx.console().println(writer.getBuffer().toString());
      }
      ctx.onFinishCommand(this, command);
    }
  }
  
  public void help() {
  }
  
  static public CommandGroup[] loadByAnnotation(String ... packages) {
    List<CommandGroup> holder = new ArrayList<CommandGroup>() ;
    for(String pkg : packages) {
      Reflections reflections = new Reflections(pkg);
      Set<Class<?>> annotateds = reflections.getTypesAnnotatedWith(CommandGroupConfig.class);
      for(Class<?> clazz : annotateds) {
        try {
          CommandGroup cgroup  = (CommandGroup) clazz.newInstance();
          CommandGroupConfig config = clazz.getAnnotation(CommandGroupConfig.class) ;
          cgroup.setName(config.name());
          holder.add(cgroup) ;
        } catch (InstantiationException | IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
    return holder.toArray(new CommandGroup[holder.size()]) ;
  }
}
