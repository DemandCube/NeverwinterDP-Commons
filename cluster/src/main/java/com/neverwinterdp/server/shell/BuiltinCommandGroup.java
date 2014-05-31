package com.neverwinterdp.server.shell;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.neverwinterdp.util.text.StringUtil;

public class BuiltinCommandGroup extends CommandGroup {

  public BuiltinCommandGroup() {
    super(":");
    add("assert", Assert.class) ;
    add("exit", Exit.class) ;
    add("set", Set.class) ;
    add("echo", Echo.class) ;
    add("sleep", Sleep.class) ;
    add("connect", Connect.class) ;
  }

  @Parameters(commandDescription = "Exit the shell")
  static public class Exit extends Command {
    public void execute(ShellContext context) {
      System.exit(0);
    }
  }
  
  @Parameters(commandDescription = "Sleep for an amount of milli seconds")
  static public class Sleep extends Command {
    @Parameter(description = "An amount of time in milli second")
    List<Long> times = new ArrayList<Long>()  ;

    public void execute(ShellContext context) throws Exception {
      if(times.size() > 0) {
        long time = times.get(0) ;
        if(time > 0) Thread.sleep(time) ;
      }
    }
  }
  
  @Parameters(commandDescription = "Set a variable in the shell context")
  static public class Set extends Command {
    @Parameter( names = {"-t", "--type"},  description = "Variable type")
    String type = "string" ;
    
    @Parameter(description = "Set name=value ")
    private List<String> pairs = new ArrayList<String>() ;
     

    
    public void execute(ShellContext context) {
      String[] pair = StringUtil.toStringArray(pairs.get(0), "=") ;
      String name = pair[0] ;
      String valueString = pair[1] ;
      Object value = valueString ;
      if("int".equalsIgnoreCase(type)) {
        value = Integer.parseInt(valueString) ;
      } else if("double".equalsIgnoreCase(type)) {
        value = Double.parseDouble(valueString) ;
      } else if("boolean".equalsIgnoreCase(type)) {
        value = Boolean.parseBoolean(valueString) ;
      }
      context.getVariables().put(name, value) ;
    }
  }
  
  @Parameters(commandDescription = "Print a text on the console")
  static public class Echo extends Command {
    @Parameter(description = "Set of tokens to print")
    private List<String> tokens ;
    
    public void execute(ShellContext ctx) {
      String[] array = tokens.toArray(new String[tokens.size()]) ;
      for(int i = 0; i < array.length; i++) {
        if(array[i].startsWith("\"") && array[i].endsWith("\"")) {
          array[i] = array[i].substring(1, array[i].length() - 1) ;
        }
      }
      ctx.console().println(array);
    }
  }
  
  static public class Assert extends Command {
    @Parameter(
      names = {"-last", "--last-command-output"}, 
      description = "Select last output result"
    )
    boolean lastOutput = true;
    
    @Parameter(
      names = {"--exit-on-fail"},  description = "Exit Shell"
    )
    boolean exitOnFail = true;
    
    @Parameter(names = {"-l", "--line"}, variableArity = true, description = "the member list in host:port format")
    List<String> lineExp  = new ArrayList<String>();
    
    public void execute(ShellContext ctx) throws Exception {
      LineMatcher lmatcher = new LineMatcher() ;
      List<LineMatcher> lineMatchers = new ArrayList<LineMatcher>() ;
      for(int i = 0; i < lineExp.size(); i++) {
        boolean end = lmatcher.add(lineExp.get(i));
        if(end) {
          lineMatchers.add(lmatcher) ;
          lmatcher = new LineMatcher() ;
        }
      }
      String text = "" ;
      if(lastOutput) text = ctx.getLastExecuteContext().getConsoleOutput() ;
      String[] line = StringUtil.splitAsArray(text, '\n', '\0');
      boolean pass = true ;
      ctx.console().header("Assert Result:");
      for(LineMatcher sel : lineMatchers) {
        if(!sel.verify(ctx, line)) {
          pass = false ;
        }
      }
      
      if(!pass) {
        ctx.console().println("Fail to assert!");
        if(exitOnFail) {
          ctx.console().println("Exit on fail to assert!");
          System.exit(0);
        } else {
          throw new Exception("Fail to assert!") ;
        }
      }
    }
  }
  
  static class LineMatcher {
    private int expect ;
    private List<Pattern> patterns = new ArrayList<Pattern>() ;
    
    public boolean add(String exp) {
      if(exp == null || exp.length() == 0) return false ;
      try {
        expect = Integer.parseInt(exp) ;
        return true ;
      } catch(NumberFormatException ex) {
      }
      Pattern pattern = Pattern.compile(exp) ;
      patterns.add(pattern) ;
      return false ;
    }
    
    public boolean verify(ShellContext ctx, String[] line) {
      int matchCount = 0 ;
      for(String selLine : line) {
        boolean match = true ;
        for(int i = 0; i < patterns.size(); i++) {
          Pattern pattern = patterns.get(i) ;
          if(!pattern.matcher(selLine).matches()) {
            match = false ;
            break ;
          }
        }
        if(match) matchCount++ ;
      }
      boolean matchLine = matchCount == expect ;
      String info = "Match --line " + StringUtil.join(patterns, " ") + " " + expect ;
      if(!matchLine) {
        ctx.console().println("  ", info," - FAIL", "(Expect ", expect, " but found ", matchCount, ")") ;
      } else {
        ctx.console().println("  ", info, " - OK") ;
      }
      return matchLine ;
    }
  }
  
  @Parameters(commandDescription = "Connect to a cluster")
  static public class Connect extends Command {
    @ParametersDelegate
    MemberSelectorOption memberSelector = new MemberSelectorOption();
    
    public void execute(ShellContext ctx) {
      ctx.connect(memberSelector.member) ;
      ctx.console().println("Connect Successfully to " + memberSelector.member);
    }
  }
}
