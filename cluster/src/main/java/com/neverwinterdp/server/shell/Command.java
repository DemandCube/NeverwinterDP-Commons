package com.neverwinterdp.server.shell;

abstract public class Command {
  abstract public void execute(ShellContext context) throws Exception ;
}