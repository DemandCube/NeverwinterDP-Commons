package com.neverwinterdp.server.gateway;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;

public class Command {
  private String commandLine ;
  private String command ;
  private String subCommand;
  
  private MemberSelector memberSelector = new MemberSelector();
  private List<String> remainOptions ;
  
  public Command(String cmdLine) {
    this(cmdLine, true) ;
  }
  
  public Command(String cmdLine, boolean parseSubCommand) {
    this.commandLine = cmdLine ;
    String[] args = parseArgs(cmdLine);
    command = args[0] ;
    args = shift(args) ;
    if(args == null) return ;
    if(parseSubCommand && !args[0].startsWith("-")) {
      subCommand = args[0] ;
      args = shift(args) ;
    }
    if(args == null) return ;
    remainOptions = Arrays.asList(args) ;
    mapPartial(memberSelector) ;
  }
  
  public String getCommandLine() { return this.commandLine ; }
  
  public String getCommand() { return this.command ; }
  public void setCommand(String command) {
    this.command = command ;
  }
  
  public String getSubCommand() { return this.subCommand ; }
  
  public void setSubCommand(String cmd) {
    this.subCommand = cmd ;
  }
  
  public List<String> getRemainOptions() { return this.remainOptions ; }
  
  public String[] getRemainOptionsAsArray() { 
    return remainOptions.toArray(new String[remainOptions.size()]) ; 
  }
  
  public MemberSelector getMemberSelector() { return this.memberSelector ; }

  public <T> void mapPartial(T object) {
    ParameterMapper mapper = new ParameterMapper() ;
    remainOptions = mapper.map(object, remainOptions) ;
  }
  
  public <T> void mapAll(T object) {
    String[] args = remainOptions.toArray(new String[remainOptions.size()]) ;
    JCommander jcommander = new JCommander(object, args) ;
    remainOptions.clear();
  }
  
  static public String[] shift(String[] array){
    if(array == null || array.length == 0) return null ;
    String[] newArray = new String[array.length - 1] ;
    System.arraycopy(array, 1, newArray, 0, newArray.length);
    return newArray ;
  }
  
  static public String[] parseArgs(String line) {
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
  
  static class ParameterMapper {
    @ParametersDelegate
    Object object ;
    
    @Parameter(description = "main parameter")
    private List<String> mainParameters ;
    
    public List<String> map(Object object, List<String> argsList) {
      this.object = object ;
      mainParameters = new ArrayList<String>();
      String[] args = argsList.toArray(new String[argsList.size()]) ;
      JCommander jcommander = new JCommander(this) ;
      jcommander.setAcceptUnknownOptions(true);
      jcommander.parse(args);
      List<String> remainOptions = new ArrayList<String>() ;
      remainOptions.addAll(jcommander.getUnknownOptions()) ;
      remainOptions.addAll(mainParameters) ;
      return remainOptions ;
    }
  }
}
