package com.neverwinterdp.server.shell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.neverwinterdp.util.IOUtil;
import com.neverwinterdp.util.text.StringUtil;

public class Shell {
  private ShellContext context = new ShellContext() ;
  private CommandGroup builtin = new BuiltinCommandGroup();
  private Map<String, CommandGroup> commandGroups = new HashMap<String, CommandGroup>() ;
  
  public Shell() {
    CommandGroup[] groups = CommandGroup.loadByAnnotation("com.neverwinterdp.server.shell") ;
    for(CommandGroup sel : groups) {
      commandGroups.put(sel.getName(), sel) ;
    }
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
  
  public void executeScript(String script) {
    String[] line = parseScript(script) ;
    for(String selLine : line) {
      execute(selLine) ;
    }
  }
  
  public void executeJSScript(String script) {
  }
  
  public void close() {
    context.close();
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
    while (m.find()) {
      String arg = m.group(1).trim() ;
      if(arg.length() == 0) continue ;
      //Add .replace("\"", "") to remove surrounding quotes.
      if(arg.startsWith("\"") && arg.endsWith("\"")) {
        arg = arg.substring(1, arg.length() - 1) ;
      }
      holder.add(arg); 
    }
    return holder.toArray(new String[holder.size()]) ;
  }

  public String[] parseScript(String script) {
    List<String> holder = new ArrayList<String>() ;
    String[] line = StringUtil.splitAsArray(script, '\n');
    StringBuilder b = new StringBuilder() ;
    for(String selLine : line) {
      selLine = selLine.trim() ;
      if(selLine.length() == 0) continue ;
      if(selLine.startsWith("#")) continue ;
      boolean endLine = !selLine.endsWith("\\") ;
      if(!endLine) {
        b.append(selLine.substring(0, selLine.length() - 1)).append('\n') ;
      } else {
        b.append(selLine) ;
        holder.add(b.toString()) ;
        b = new StringBuilder() ;
      }
    }
    return holder.toArray(new String[holder.size()]) ;
  }

  static public class Options {
    @Parameter(names = {"--connect"}, description = "Connect in the host:port format")
    String connect = "" ;
    
    @Parameter(names = {"-f", "--script-file"}, description = "Run the script file")
    String script ;
    
    @Parameter(
      names = {"-c", "--command"}, variableArity = true, 
      description = "Run the script command"
    )
    List<String> tokens = new ArrayList<String>() ;
  }
  
  static public void main(String[] args) throws Exception {
    Options options = new Options() ;
    new JCommander(options, args) ;
    Shell shell = new Shell() ;
    if(options.connect != null) {
      shell.getShellContext().connect(options.connect);
    } else {
      shell.getShellContext().connect((String[])null);
    }
    
    if(options.tokens.size() > 0) {
      String command = StringUtil.join(options.tokens, " ") ;
      shell.execute(command);
    } 
   
    if(options.script != null) {
      String script = IOUtil.getFileContentAsString(options.script) ;
      shell.executeScript(script);
    }
    shell.getShellContext().close();
  }
}