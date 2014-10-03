package com.neverwinterdp.server.shell;

import com.neverwinterdp.server.command.ServiceCommandResult;
import com.neverwinterdp.server.gateway.Command;
import com.neverwinterdp.yara.cluster.ClusterMetricPrinter;
import com.neverwinterdp.yara.snapshot.ClusterMetricRegistrySnapshot;

@ShellCommandConfig(name = "yara")
public class YaraCommand extends ShellCommand {
  public YaraCommand() {
    add("snapshot", snapshot.class);
  }
  
  static public class snapshot extends ShellSubCommand {
    public void execute(Shell shell, ShellContext ctx, Command command) throws Exception {
      ServiceCommandResult<ClusterMetricRegistrySnapshot>[] results = ctx.getClusterGateway().execute(command) ;
      ctx.console().header("Cluster Metric Snapshot");
      ClusterMetricPrinter printer = new ClusterMetricPrinter(ctx.console().getConsoleAppendable()) ;
      for(int i = 0; i < results.length; i++) {
        ServiceCommandResult<ClusterMetricRegistrySnapshot> sel = results[i] ;
        if(sel.hasError()) {
          ctx.console().println(sel.getError());
        } else {
          printer.print(sel.getResult());
        }
      }
    }
  }
}