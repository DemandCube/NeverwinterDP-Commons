package com.neverwinterdp.server.shell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.neverwinterdp.server.gateway.Command;
import com.neverwinterdp.util.IOUtil;
import com.neverwinterdp.util.text.StringUtil;

public class Shell {
  private ShellContext context = new ShellContext() ;
  private ShellCommand builtin = new ShellBuiltinCommand();
  private Map<String, ShellCommand> commands = new HashMap<String, ShellCommand>() ;
  
  public Shell() {
    ShellCommand[] groups = ShellCommand.loadByAnnotation("com.neverwinterdp.server.shell") ;
    for(ShellCommand sel : groups) {
      commands.put(sel.getName(), sel) ;
    }
  }
  
  public ShellContext getShellContext() { return this.context ; }
  
  public void execute(String line) {
    line = processVariables(line) ;
    Command command = null; 
    if(line.startsWith(":")) command = new Command(line,false) ;
    else command = new Command(line) ;
    
    if("help".equalsIgnoreCase(command.getCommand())) {
      builtin.help();
      return ;
    }
    
    try {
      String cmdName = command.getCommand() ;
      if(cmdName.startsWith(":")) {
        command.setCommand("default");
        command.setSubCommand(cmdName.substring(1));
        builtin.execute(context, command);
      } else {
        ShellCommand shellCommand = commands.get(cmdName) ;
        if(shellCommand != null) {
          shellCommand.execute(context, command);
        } else {
          throw new Exception("Unknown: " + command.getCommandLine()) ;
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