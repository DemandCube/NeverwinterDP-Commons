package com.neverwinterdp.server.cluster.shell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shell {
  private ShellContext context = new ShellContext() ;
  private CommandGroup builtin = new BuiltinCommandGroup();
  private Map<String, CommandGroup> commandGroups = new HashMap<String, CommandGroup>() ;
  
  public Shell() {
    commandGroups.put("cluster", new ClusterCommandGroup()) ;
  }
  
  public ShellContext getShellContext() { return this.context ; }
  
  public void execute(String line) {
    line = processVariables(line) ;
    String[] args = parseArgs(line);
    String command = args[0] ;
    if("help".equalsIgnoreCase(command)) {
      builtin.help();
      return ;
    }
    
    try {
      if(command.startsWith(":")) {
        args[0] = command.substring(1) ;
        builtin.execute(context, args);
      } else {
        String[] newargs = new String[args.length - 1] ;
        System.arraycopy(args, 1, newargs, 0, newargs.length);
        CommandGroup group = commandGroups.get(command) ;
        if(group != null) {
          group.execute(context, newargs);
        } else {
        }
      }
    } catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  
  private String processVariables(String line) {
    for(Map.Entry<String, Object> entry : context.getVariables().entrySet()) {
      String name = "$" + entry.getKey() ;
      String value = entry.getValue().toString() ;
      line = line.replace(name, value) ;
    }
    return line ;
  }
  
  private String[] parseArgs(String line) {
    List<String> holder = new ArrayList<String>();
    Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(line);
    while (m.find())
      holder.add(m.group(1).trim()); // Add .replace("\"", "") to remove surrounding quotes.
    return holder.toArray(new String[holder.size()]) ;
  }

  static public void main(String[] args) {
    
  }
}
