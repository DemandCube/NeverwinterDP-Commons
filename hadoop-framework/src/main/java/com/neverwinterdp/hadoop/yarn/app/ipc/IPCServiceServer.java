package com.neverwinterdp.hadoop.yarn.app.ipc;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.io.serializer.JavaSerialization;
import org.apache.hadoop.io.serializer.WritableSerialization;
import org.apache.hadoop.io.serializer.avro.AvroSerialization;
import org.apache.hadoop.ipc.RPC;

import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerInfo;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerProgressStatus;

public class IPCServiceServer {
  private AppMaster appMaster ;
  private Map<String, ReportHandler> handlers = new HashMap<String, ReportHandler>() ;
  private RPC.Server rpcServer ;
  
  public IPCServiceServer(AppMaster appMaster) throws Exception {
    this.appMaster = appMaster ;
    Configuration rpcConf = new Configuration() ;
    rpcConf.set(
      CommonConfigurationKeys.IO_SERIALIZATIONS_KEY, 
      JavaSerialization.class.getName() + "," + 
      WritableSerialization.class.getName() + "," +
      AvroSerialization.class.getName()
    ) ;
    rpcServer = 
        new RPC.Builder(rpcConf).
        setInstance(new IPCServiceImpl()).
        setProtocol(IPCService.class).
        setBindAddress(InetAddress.getLocalHost().getHostAddress()).
        setPort(appMaster.getAppInfo().appRpcPort).
        build();
    rpcServer.start() ;
  }
  
  public void register(String name, ReportHandler handler) {
    handlers.put(name, handler) ;
  }
  
  public int getListenPort() {
    return rpcServer.getListenerAddress().getPort() ;
  }
  
  public String getHostAddress() {
    return rpcServer.getListenerAddress().getAddress().getHostAddress();
  }
  
  public void shutdown() {
    rpcServer.stop() ;
  }
    
  public class IPCServiceImpl implements IPCService {
    public String ping(String msg) { return msg ; }
    
    public void   report(int containerId, AppWorkerContainerProgressStatus status) {
      AppWorkerContainerInfo containerInfo = appMaster.getAppMonitor().getContainerInfo(containerId) ;
      containerInfo.setProgressStatus(status);
    }  

    public void report(String name, int containerId, ReportData data) {
      AppWorkerContainerInfo containerInfo = appMaster.getAppMonitor().getContainerInfo(containerId) ;
      ReportHandler handler = handlers.get(name) ;
      if(handler != null) {
        handler.onReport(appMaster, containerInfo, data);
      }
    }
    
    public Response getMonitorData() {
      return new Response(appMaster.getAppMonitor()) ;
    }
  }
}