package com.neverwinterdp.hadoop.yarn;

import java.io.IOException;
import java.util.Date;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;

import com.neverwinterdp.util.text.StringUtil;
import com.neverwinterdp.util.text.TabularPrinter;

public class AppClientReporter {
  private YarnClient yarnClient;
  private ApplicationId appId;
  
  public AppClientReporter(YarnClient yarnClient, ApplicationId appId) {
    this.yarnClient = yarnClient ;
    this.appId = appId ;
  }
  
  public YarnClient getYarnClient() { return this.yarnClient ; }
  
  public ApplicationId getApplicationId() { return this.appId ; }
  
  public ApplicationReport getApplicationReport() throws YarnException, IOException { 
    return yarnClient.getApplicationReport(appId) ; 
  }
  
  public void monitor() throws Exception {
    ApplicationReport appReport = yarnClient.getApplicationReport(appId);
    YarnApplicationState appState = appReport.getYarnApplicationState();
    while (appState != YarnApplicationState.FINISHED && 
           appState != YarnApplicationState.KILLED && 
           appState != YarnApplicationState.FAILED) {
      Thread.sleep(100);
      appReport = yarnClient.getApplicationReport(appId);
      appState = appReport.getYarnApplicationState();
    }
    System.out.println(
      "Application " + appId + " finished with state " + appState + 
      " at " + appReport.getFinishTime()
    );
  }
  
  public void report(Appendable out) throws IOException, YarnException {
    ApplicationReport report = yarnClient.getApplicationReport(appId);
    int[] colWidth = {30, 30} ;
    TabularPrinter printer = new TabularPrinter(out, colWidth) ;
    printer.row("Application Id", report.getApplicationId());
    printer.row("Application Name", report.getName());
    printer.row("Application Tags", StringUtil.join(report.getApplicationTags(), ","));
    printer.row("Progress", report.getProgress());
    printer.row("Start Time", new Date(report.getStartTime()));
    printer.row("Finish Time", new Date(report.getFinishTime()));
    printer.row("Application Type", report.getApplicationType());
    printer.row("Yarn Application State", report.getYarnApplicationState());
    printer.row("Final Application Status", report.getFinalApplicationStatus());
    printer.row("Original Tracking Url", report.getOriginalTrackingUrl());
    printer.row("Tracking Url", report.getTrackingUrl()) ;
    printer.row("Queue", report.getQueue());
    printer.row("Diagnostics", report.getDiagnostics());
    
    ApplicationResourceUsageReport usageReport = report.getApplicationResourceUsageReport() ;
    printer.row("Application Resource Usage Report", "");
    printer.row("Reserved Containers", usageReport.getNumReservedContainers());
    printer.row("Used Containers", usageReport.getNumUsedContainers());
    Resource neededResource = usageReport.getNeededResources() ;
    printer.row("Needed Resources", "");
    printer.row("Memory", neededResource.getMemory());
    printer.row("Virtual Cores", neededResource.getVirtualCores());
    Resource usedResource = usageReport.getUsedResources() ;
    printer.row("Used Resources", "");
    printer.row("Memory", usedResource.getMemory());
    printer.row("Virtual Cores", usedResource.getVirtualCores());
  }
}