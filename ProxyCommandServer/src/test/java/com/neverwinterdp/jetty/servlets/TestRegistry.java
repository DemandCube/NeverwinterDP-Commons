package com.neverwinterdp.jetty.servlets;

import org.junit.Test;

import com.neverwinterdp.registry.Registry;
import com.neverwinterdp.registry.RegistryConfig;
import com.neverwinterdp.registry.RegistryException;
import com.neverwinterdp.registry.zk.RegistryImpl;

public class TestRegistry {
  
  private Registry newRegistry() {
    return new RegistryImpl(RegistryConfig.getDefault()) ;
  }
  
  
  /**
   * Example registry
   * Need to get master to say what it's hostname/IP is
scribengin
  master
    leader - {"storedPath":"/vm/allocated/vm-scribengin-master-1","memory":128,"cpuCores":
      leader-0000000000
  dataflows
    test-dataflow - {"name":"test-dataflow","sourceDescriptor":{"location":"/data/source","type":
      tasks
        locks
        assigned
        finished
          task-0000000008 - {"id":8,"status":"INIT","dataProcessor":"com.neverwinterdp.scribengin.Main$Te
          task-0000000009 - {"id":9,"status":"INIT","dataProcessor":"com.neverwinterdp.scribengin.Main$Te
          task-0000000012 - {"id":12,"status":"INIT","dataProcessor":"com.neverwinterdp.scribengin.Main$T
          task-0000000003 - {"id":3,"status":"INIT","dataProcessor":"com.neverwinterdp.scribengin.Main$Te
          task-0000000002 - {"id":2,"status":"INIT","dataProcessor":"com.neverwinterdp.scribengin.Main$Te
          task-0000000011 - {"id":11,"status":"INIT","dataProcessor":"com.neverwinterdp.scribengin.Main$T
          task-0000000014 - {"id":14,"status":"INIT","dataProcessor":"com.neverwinterdp.scribengin.Main$T
          task-0000000001 - {"id":1,"status":"INIT","dataProcessor":"com.neverwinterdp.scribengin.Main$Te
          task-0000000013 - {"id":13,"status":"INIT","dataProcessor":"com.neverwinterdp.scribengin.Main$T
          task-0000000000 - {"id":0,"status":"INIT","dataProcessor":"com.neverwinterdp.scribengin.Main$Te
          task-0000000007 - {"id":7,"status":"INIT","dataProcessor":"com.neverwinterdp.scribengin.Main$Te
          task-0000000006 - {"id":6,"status":"INIT","dataProcessor":"com.neverwinterdp.scribengin.Main$Te
          task-0000000005 - {"id":5,"status":"INIT","dataProcessor":"com.neverwinterdp.scribengin.Main$Te
          task-0000000004 - {"id":4,"status":"INIT","dataProcessor":"com.neverwinterdp.scribengin.Main$Te
          task-0000000010 - {"id":10,"status":"INIT","dataProcessor":"com.neverwinterdp.scribengin.Main$T
        available
      workers
        test-dataflow-worker-1 - {"storedPath":"/vm/allocated/test-dataflow-worker-1","memory":128,"cpuCores":
      master
        leader - {"storedPath":"/vm/allocated/test-dataflow-master-1","memory":128,"cpuCores":
          leader-0000000000
vm
  history
  allocated
      test-dataflow-master-1 - {"storedPath":"/vm/allocated/test-dataflow-master-1","memory":128,"cpuCores":
        status - "RUNNING"
          heartbeat
        commands
      vm-scribengin-master-1 - {"storedPath":"/vm/allocated/vm-scribengin-master-1","memory":128,"cpuCores":
        status - "RUNNING"
          heartbeat
        commands
      VMMaster - {"storedPath":"/vm/allocated/VMMaster","memory":128,"cpuCores":1,"hostname":n
        status - "RUNNING"
          heartbeat
        commands
      test-dataflow-worker-1 - {"storedPath":"/vm/allocated/test-dataflow-worker-1","memory":128,"cpuCores":
        status - "RUNNING"
          heartbeat
        commands
  master
      leader - {"storedPath":"/vm/allocated/VMMaster","memory":128,"cpuCores":1,"hostname":n
        leader-0000000000

   * @throws RegistryException
   */
  @Test
  public void testRegistry() throws RegistryException{
    Registry registry = newRegistry().connect();
  }
}
