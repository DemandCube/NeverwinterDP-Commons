package com.neverwinterdp.util.monitor.snapshot;

import java.text.DecimalFormat;
import java.util.Map;

import com.neverwinterdp.util.text.TabularPrinter;

public class MetricFormater {
  private String indent = null ;

  public MetricFormater() {
  }
  
  public MetricFormater(String indent) {
    this.indent = indent ;
  }
  
  public String format(Map<String, TimerSnapshot> map) {
    StringBuilder b = new StringBuilder() ;
    String[] header = {
      "name",
      "count", "max", "mean", "min",
      "p50", "p75", "p95", "p98", "p99", "p999",
      "stddev", "m15_rate", "m1_rate", "m5_rate", "mean_rate",
      "d_units", "r_units"
    } ;
    int[] width = {
      30,
      6, 6, 6, 6,
      6, 6, 6, 6, 6, 6,
      6, 6, 6, 6, 6,
      8, 8
    } ;
    TabularPrinter printer = new TabularPrinter(b, width) ;
    printer.setIndent(indent) ;
    printer.header(header);
    DecimalFormat dFormater = new DecimalFormat( "#0.00" );
    for(Map.Entry<String, TimerSnapshot> entry : map.entrySet()) {
      TimerSnapshot sel = entry.getValue() ;
      String name = entry.getKey() ;
      if(name.length() > 30) {
        name = "..." + name.substring(name.length() - 27) ;
      }
      long count = sel.getCount() ;
      long max = sel.getMax() ;
      String mean = dFormater.format(sel.getMean()) ;
      long   min  = sel.getMin() ;
      String p50 = dFormater.format(sel.getP50()) ;
      String p75 = dFormater.format(sel.getP75()) ;
      String p95 = dFormater.format(sel.getP95()) ;
      String p98 = dFormater.format(sel.getP98()) ;
      String p99 = dFormater.format(sel.getP99()) ;
      String p999 = dFormater.format(sel.getP999()) ;
      String stddev = dFormater.format(sel.getStddev()) ;
      String m15_rate = dFormater.format(sel.getM15Rate()) ;
      String m1_rate = dFormater.format(sel.getM1Rate()) ;
      String m5_rate = dFormater.format(sel.getM5Rate()) ;
      String mean_rate = dFormater.format(sel.getMeanRate()) ;
      String duration_units = sel.getDurationUnits();
      String rate_units = sel.getRateUnits();
      printer.row(
        name,
        count, max, mean, min,
        p50, p75, p95, p98, p99, p999,
        stddev, m15_rate, m1_rate, m5_rate, mean_rate,
        "ms", "call/s"
      );
    }
    return b.toString() ;
  }
}
