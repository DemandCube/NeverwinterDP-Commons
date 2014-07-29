package com.neverwinterdp.server.shell;

import com.neverwinterdp.server.gateway.Command;

abstract public class ShellSubCommand {
  abstract public void execute(Shell shell, ShellContext context, Command command) throws Exception ;
}