package com.neverwinterdp.server.shell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.neverwinterdp.server.gateway.Command;
import com.neverwinterdp.server.shell.js.ScriptRunner;
import com.neverwinterdp.util.text.StringUtil;

public class ShellBuiltinCommand extends ShellCommand {

  public ShellBuiltinCommand() {
    super(":");
    add("assert", Assert.class);
    add("exit", Exit.class);
    add("set", Set.class);
    add("echo", Echo.class);
    add("sleep", Sleep.class);
    add("jsrun", JSRun.class);
    add("connect", Connect.class);
  }

  @Parameters(commandDescription = "Exit the shell")
  static public class Exit extends ShellSubCommand {
    public void execute(Shell shell, ShellContext context, Command command) {
      System.exit(0);
    }
  }

  @Parameters(commandDescription = "Sleep for an amount of milli seconds")
  static public class Sleep extends ShellSubCommand {
    @Parameter(description = "An amount of time in milli second")
    List<Long> times = new ArrayList<Long>();

    public void execute(Shell shell, ShellContext context, Command command) throws Exception {
      command.mapAll(this);
      if (times.size() > 0) {
        long time = times.get(0);
        if (time > 0)
          Thread.sleep(time);
      }
    }
  }

  @Parameters(commandDescription = "Set a variable in the shell context")
  static public class Set extends ShellSubCommand {
    @Parameter(names = { "-t", "--type" }, description = "Variable type")
    String               type  = "string";

    @Parameter(description = "Set name=value ")
    private List<String> pairs = new ArrayList<String>();

    public void execute(Shell shell, ShellContext context, Command command) {
      command.mapAll(this);
      String[] pair = StringUtil.toStringArray(pairs.get(0), "=");
      String name = pair[0];
      String valueString = pair[1];
      Object value = valueString;
      if ("int".equalsIgnoreCase(type)) {
        value = Integer.parseInt(valueString);
      } else if ("double".equalsIgnoreCase(type)) {
        value = Double.parseDouble(valueString);
      } else if ("boolean".equalsIgnoreCase(type)) {
        value = Boolean.parseBoolean(valueString);
      }
      context.getVariables().put(name, value);
    }
  }

  @Parameters(commandDescription = "Print a text on the console")
  static public class Echo extends ShellSubCommand {
    @Parameter(description = "The tokens to print")
    private List<String> tokens = new ArrayList<String>();

    public void execute(Shell shell, ShellContext ctx, Command command) {
      command.mapAll(this);
      String[] array = tokens.toArray(new String[tokens.size()]);
      for (int i = 0; i < array.length; i++) {
        if (array[i].startsWith("\"") && array[i].endsWith("\"")) {
          array[i] = array[i].substring(1, array[i].length() - 1);
        }
      }
      ctx.console().println(array);
    }
  }

  @Parameters(commandDescription = "Run a javascript file")
  static public class JSRun extends ShellSubCommand {
    @DynamicParameter(names = "-P", description = "The dynamic properties for the script")
    private Map<String, String> properties = new HashMap<String, String>();
    
    @Parameter(description = "list of the js file")
    List<String> files = new ArrayList<String>();

    public void execute(Shell shell, ShellContext context, Command command) throws Exception {
      command.mapAll(this);
      HashMap<String, Object> ctx = new HashMap<String, Object>();
      ctx.put("JAVA_CLUSTER_GATEWAY", context.getClusterGateway());
      ctx.put("SHELL", shell);
      ctx.putAll(properties);
      String jsDir = ".";
      String appDir = System.getProperty("app.dir", null);
      if (appDir != null) jsDir = appDir + "/jscript";
      ScriptRunner runner = new ScriptRunner(jsDir, ctx);
      for (String selFile : files) {
        runner.runScript(selFile);
      }
    }
  }

  static public class Assert extends ShellSubCommand {
    @Parameter(
        names = { "-last", "--last-command-output" },
        description = "Select last output result")
    boolean      lastOutput = true;

    @Parameter(
        names = { "--exit-on-fail" }, description = "Exit Shell")
    boolean      exitOnFail = true;

    @Parameter(names = { "-l", "--line" }, variableArity = true, description = "the member list in host:port format")
    List<String> lineExp    = new ArrayList<String>();

    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      command.mapAll(this);
      LineMatcher lmatcher = new LineMatcher();
      List<LineMatcher> lineMatchers = new ArrayList<LineMatcher>();
      for (int i = 0; i < lineExp.size(); i++) {
        boolean end = lmatcher.add(lineExp.get(i));
        if (end) {
          lineMatchers.add(lmatcher);
          lmatcher = new LineMatcher();
        }
      }
      String text = "";
      if (lastOutput)
        text = ctx.getLastExecuteContext().getConsoleOutput();
      String[] line = StringUtil.splitAsArray(text, '\n', '\0');
      boolean pass = true;
      ctx.console().header("Assert Result:");
      for (LineMatcher sel : lineMatchers) {
        if (!sel.verify(ctx, line)) {
          pass = false;
        }
      }

      if (!pass) {
        ctx.console().println("Fail to assert!");
        if (exitOnFail) {
          ctx.console().println("Exit on fail to assert!");
          System.exit(0);
        } else {
          throw new Exception("Fail to assert!");
        }
      }
    }
  }

  static class LineMatcher {
    private int           expect;
    private List<Pattern> patterns = new ArrayList<Pattern>();

    public boolean add(String exp) {
      if (exp == null || exp.length() == 0)
        return false;
      try {
        expect = Integer.parseInt(exp);
        return true;
      } catch (NumberFormatException ex) {
      }
      Pattern pattern = Pattern.compile(exp);
      patterns.add(pattern);
      return false;
    }

    public boolean verify(ShellContext ctx, String[] line) {
      int matchCount = 0;
      for (String selLine : line) {
        boolean match = true;
        for (int i = 0; i < patterns.size(); i++) {
          Pattern pattern = patterns.get(i);
          if (!pattern.matcher(selLine).matches()) {
            match = false;
            break;
          }
        }
        if (match)
          matchCount++;
      }
      boolean matchLine = matchCount == expect;
      String info = "Match --line " + StringUtil.join(patterns, " ") + " " + expect;
      if (!matchLine) {
        ctx.console().println("  ", info, " - FAIL", "(Expect ", expect, " but found ", matchCount, ")");
      } else {
        ctx.console().println("  ", info, " - OK");
      }
      return matchLine;
    }
  }

  @Parameters(commandDescription = "Connect to a cluster")
  static public class Connect extends ShellSubCommand {
    @Parameter(description = "Select the member by host:port")
    List<String> connects = new ArrayList<String>();

    public void execute(Shell shell, ShellContext ctx, Command command) {
      ctx.connect(connects.toArray(new String[connects.size()]));
      ctx.console().println("Connect Successfully to " + connects);
    }
  }
}
