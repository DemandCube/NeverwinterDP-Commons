package com.neverwinterdp.util.text;

import java.util.ArrayList;
import java.util.List;

/**
 * $Author: Tuan Nguyen$
 **/
public class TabularFormater {
  private String         indent  ;
  private String[]       headers ;
  private List<String[]> rows   ;

  public TabularFormater(String ... header) {
    this.headers = header ;
    this.rows = new ArrayList<String[]>() ;
  }
  
  public void setIndent(String indent) { this.indent = indent ; }

  public void addRow(Object ... cellData) {
    if(cellData.length != headers.length) {
      throw new RuntimeException("Expect " + headers.length + " cells insteader of " + cellData.length) ;
    }
    String[] cell = new String[cellData.length] ;
    for(int i = 0; i < cell.length; i++) {
      if(cellData[i] == null) cell[i] = "" ;
      else cell[i] = cellData[i].toString() ;
    }
    rows.add(cell) ;
  }

  public String getFormatText() {
    StringBuilder b = new StringBuilder() ;
    print(b) ;
    return b.toString() ;
  }
  
  public void print(Appendable out) {
    int[] width = new int[headers.length] ;
    for(int i = 0; i < width.length; i++) {
      width[i] = headers[i].length() ;
    }
    
    for(int i = 0; i < rows.size(); i++) {
      String[] cell = rows.get(i) ;
      for(int j = 0; j < cell.length; j++) {
        if(cell[j].length() > width[j]) {
          width[j] = cell[j].length() ;
        }
      }
    }
    
    print(out, indent) ;
    int lineLength = 0 ;
    for(int i = 0; i < headers.length; i++) {
      printCell(out, headers[i], width[i]) ;
      lineLength += width[i] + 3 ;
    }
    print(out, "\n") ;
    
    print(out, indent) ;
    for(int i = 0; i < lineLength; i++) {
      print(out, "-") ;
    }
    print(out, "\n") ;
    for(int i = 0; i < rows.size(); i++) {
      print(out, indent) ;
      String[] cell = rows.get(i) ;
      for(int j = 0; j < cell.length; j++) {
        printCell(out,  cell[j], width[j]) ;
      }
      print(out, "\n") ;
    }
  }
  
  private void printCell(Appendable out, String cell, int width) {
    int len = cell.length();
    if (len > 80) {
      cell = cell.substring(0, 80) + "...";
      len = cell.length();
    }
    print(out, cell);
    for (int i = len; i < width; i++) {
      print(out, " ");
    }
    print(out, "   ");
  }

  private void print(Appendable out, String string) {
    if(string == null) return ;
    try {
      out.append(string);
    } catch(Exception ex) {
      throw new RuntimeException("Append error", ex) ; 
    }
  }
  
  static public void main(String[] args) {
    String[] header = { "header 1", "header 2", "header 3" };
    TabularFormater formater = new TabularFormater(header);
    for (int i = 0; i < 10; i++) {
      formater.addRow("column 1", "this is the column 2", "my column 3");
    }
    System.out.println(formater.getFormatText());
  }
}