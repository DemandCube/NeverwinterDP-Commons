package com.neverwinterdp.server.cluster.shell;

abstract public class Command {
  abstract public void execute(ShellContext context) throws Exception ;
}