package com.neverwinterdp.hadoop.yarn.hello;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.NMClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.Records;

import com.beust.jcommander.JCommander;
import com.neverwinterdp.hadoop.yarn.AppOptions;

public class HelloApplicationMaster {
  public static void main(String[] args) throws Exception {
    System.out.println("HelloApplicationMaster: in main(..................)") ;
    AppOptions appOptions = new AppOptions() ;
    new JCommander(appOptions, args) ;
    
    // Initialize clients to ResourceManager and NodeManagers
    Configuration conf = new YarnConfiguration();
    conf.set("yarn.resourcemanager.scheduler.address", "0.0.0.0:8030") ;
    AMRMClient<ContainerRequest> rmClient = AMRMClient.createAMRMClient();
    rmClient.init(conf);
    rmClient.start();

    NMClient nmClient = NMClient.createNMClient();
    nmClient.init(conf);
    nmClient.start();

    // Register with ResourceManager
    System.out.println("HelloApplicationMaster: before registerApplicationMaster 0");
    rmClient.registerApplicationMaster("", 0, "");
    System.out.println("HelloApplicationMaster: after registerApplicationMaster 0");
    
    // Priority for worker containers - priorities are intra-application
    Priority priority = Records.newRecord(Priority.class);
    priority.setPriority(0);

    // Resource requirements for worker containers
    Resource capability = Records.newRecord(Resource.class);
    capability.setMemory(128);
    capability.setVirtualCores(1);

    // Make container requests to ResourceManager
    for(int i = 0; i < appOptions.allocateContainer; ++i) {
      ContainerRequest containerAsk = new ContainerRequest(capability, null, null, priority);
      System.out.println("Making res-req " + i);
      rmClient.addContainerRequest(containerAsk);
    }

    // Obtain allocated containers and launch 
    int allocatedContainers = 0;
    while (allocatedContainers < appOptions.allocateContainer) {
      AllocateResponse response = rmClient.allocate(0);
      for (Container container : response.getAllocatedContainers()) {
        ++allocatedContainers;
        // Launch container by create ContainerLaunchContext
        ContainerLaunchContext ctx = Records.newRecord(ContainerLaunchContext.class);
        ctx.setCommands(appOptions.buildAppCommands());
        System.out.println("Launching container " + allocatedContainers);
        nmClient.startContainer(container, ctx);
      }
      Thread.sleep(100);
    }

    // Now wait for containers to complete
    int completedContainers = 0;
    while (completedContainers < appOptions.allocateContainer) {
      AllocateResponse response = rmClient.allocate(completedContainers/appOptions.allocateContainer);
      for (ContainerStatus status : response.getCompletedContainersStatuses()) {
        ++completedContainers;
        System.out.println("Completed container " + completedContainers);
      }
      Thread.sleep(100);
    }

    // Un-register with ResourceManager
    rmClient.unregisterApplicationMaster(FinalApplicationStatus.SUCCEEDED, "", "");
  }
}
