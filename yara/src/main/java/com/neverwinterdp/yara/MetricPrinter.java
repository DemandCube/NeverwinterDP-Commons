package com.neverwinterdp.yara;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;

import com.neverwinterdp.util.text.TabularFormater;

public class MetricPrinter {
  private Appendable out = System.out ;
  
  public void print(MetricRegistry registry) throws IOException {
    printCounters(registry.getCounters()) ;
    printTimers(registry.getTimers()) ;
  }
  
  public void printTimers(Map<String, Timer> timers) throws IOException {
    TimerPrinter tprinter = new TimerPrinter(out) ;
    Iterator<Map.Entry<String, Timer>> timerItr = timers.entrySet().iterator() ;
    while(timerItr.hasNext()) {
      Map.Entry<String, Timer> entry = timerItr.next() ;
      tprinter.print(entry.getKey(), entry.getValue());
    }
    tprinter.flush(); 
  }
  
  public void printCounters(Map<String, Counter> counters) throws IOException {
    CounterPrinter cPrinter = new CounterPrinter(out) ;
    Iterator<Map.Entry<String, Counter>> timerItr = counters.entrySet().iterator() ;
    while(timerItr.hasNext()) {
      Map.Entry<String, Counter> entry = timerItr.next() ;
      cPrinter.print(entry.getKey(), entry.getValue());
    }
    cPrinter.flush(); 
  }
  
  
  static public class TimerPrinter {
    private Appendable out = System.out;
    protected DecimalFormat dFormater = new DecimalFormat("#.00");
    protected TabularFormater  tformater ;
    
    public TimerPrinter() { 
      tformater = new TabularFormater(
          "Name", "Count",
          "Min", "Max", "Mean", "Std Dev",
          "75%", "90%", "95%", "99%", "99.999",
          "1 Min", "5 Min", "15 Min", "M Rate"
      ) ;
      tformater.setTitle("Timer") ;
    }
    
    public TimerPrinter(Appendable out) { 
      this() ;
      this.out = out ; 
    }
    
    public void print(String name, Timer timer) {
      Histogram histogram = timer.getHistogram() ;
      tformater.addRow(
        name, 
        timer.getCount(),
        
        histogram.getMin(), histogram.getMax(), dFormater.format(histogram.getMean()), dFormater.format(histogram.getStdDev()), 
        
        dFormater.format(histogram.getQuantile(0.75)),
        dFormater.format(histogram.getQuantile(0.90)),
        dFormater.format(histogram.getQuantile(0.95)),
        dFormater.format(histogram.getQuantile(0.99)),
        dFormater.format(histogram.getQuantile(0.99999)),
        
        dFormater.format(timer.getOneMinuteRate()), 
        dFormater.format(timer.getFiveMinuteRate()), 
        dFormater.format(timer.getFifteenMinuteRate()),
        dFormater.format(timer.getMeanRate())
      );
    }
    
    public void flush() throws IOException {
      out.append("\n") ;
      out.append(tformater.getFormatText());
      out.append("\n") ;
    }
  }
  
  static public class CounterPrinter {
    private Appendable out = System.out;
    protected TabularFormater  tformater ;
    
    public CounterPrinter() { 
      tformater = new TabularFormater(
          "Name", "Count"
      ) ;
      tformater.setTitle("Counter") ;
    }
    
    public CounterPrinter(Appendable out) { 
      this() ;
      this.out = out ; 
    }
    
    public void print(String name, Counter timer) {
      tformater.addRow(name, timer.getCount());
    }

    public void flush() throws IOException {
      out.append("\n") ;
      out.append(tformater.getFormatText());
      out.append("\n") ;
    }
  }
}
