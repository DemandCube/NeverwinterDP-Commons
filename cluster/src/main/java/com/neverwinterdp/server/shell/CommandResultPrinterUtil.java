package com.neverwinterdp.server.shell;

import com.neverwinterdp.server.command.ServerCommandResult;
import com.neverwinterdp.server.command.ServiceCommandResult;
import com.neverwinterdp.server.gateway.Command;
import com.neverwinterdp.util.text.TabularFormater;

public class CommandResultPrinterUtil {
  static public void printPrimitiveServiceResults(ShellContext ctx, Command command, ServiceCommandResult<?>[] results, String ... explaination) {
    Console out = ctx.console() ;
    out.header(command.getCommandLine()) ;
    String indent = "     " ;
    TabularFormater tformater = new TabularFormater("Host IP", "Port", "Result") ;
    tformater.setIndent(indent) ;
    for(ServiceCommandResult<?> sel : results) {
      String result = "ERROR" ;
      if(!sel.hasError()) result = sel.getResult().toString() ;
      tformater.addRow(
        sel.getFromMember().getIpAddress(), sel.getFromMember().getPort(), result
      );
    }
    out.println(tformater.getFormatText());
    if(explaination != null) {
      for(String sel : explaination) {
        out.println(indent + "* " + sel);
      }
    }
    
    for(int i = 0; i < results.length; i++) {
      ServiceCommandResult<?> sel = results[i] ;
      if(sel.hasError()) {
        out.println("ERROR on " + (sel.getFromMember().getIpAddress() + ":" + sel.getFromMember().getPort())) ;
        out.println(sel.getError()) ;
      }
    }
  }
  
  static public void printPrimitiveServerResults(ShellContext ctx, Command command, ServerCommandResult<?>[] results, String ... explaination) {
    Console out = ctx.console() ;
    out.header(command.getCommandLine()) ;
    String indent = "     " ;
    TabularFormater tformater = new TabularFormater("Host IP", "Port", "Result") ;
    tformater.setIndent(indent) ;
    for(ServerCommandResult<?> sel : results) {
      String result = "ERROR" ;
      if(!sel.hasError()) result = sel.getResult().toString() ;
      tformater.addRow(
        sel.getFromMember().getIpAddress(), sel.getFromMember().getPort(), result
      );
    }
    out.println(tformater.getFormatText());
    if(explaination != null) {
      for(String sel : explaination) {
        out.println(indent + "* " + sel);
      }
    }
    
    for(int i = 0; i < results.length; i++) {
      ServerCommandResult<?> sel = results[i] ;
      if(sel.hasError()) {
        out.println("ERROR on " + (sel.getFromMember().getIpAddress() + ":" + sel.getFromMember().getPort())) ;
        out.println(sel.getError()) ;
      }
    }
  }
}
