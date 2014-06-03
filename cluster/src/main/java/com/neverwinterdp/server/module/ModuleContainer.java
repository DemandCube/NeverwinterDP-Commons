package com.neverwinterdp.server.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.neverwinterdp.server.RuntimeEnvironment;
import com.neverwinterdp.server.module.ModuleRegistration.InstallStatus;
import com.neverwinterdp.server.module.ModuleRegistration.RunningStatus;
import com.neverwinterdp.server.service.Service;
import com.neverwinterdp.server.service.ServiceContainer;
import com.neverwinterdp.server.service.ServiceRegistration;
import com.neverwinterdp.util.LoggerFactory;
/**
 * @author Tuan Nguyen
 * @email tuan08@gmail.com
 */
@Singleton
public class ModuleContainer {
  @Inject
  private Injector parentContainer ;
  
  @Inject
  private RuntimeEnvironment runtimeEnvironment ;
  
  private LoggerFactory loggerFactory ;
  private Logger          logger;
  
  private Map<String, ModuleRegistration> availableModules = new ConcurrentHashMap<String, ModuleRegistration>();
  private Map<String, ServiceContainer> installedModules = new ConcurrentHashMap<String, ServiceContainer>();

  @Inject
  public ModuleContainer(LoggerFactory lfactory) throws Exception {
    logger = lfactory.getLogger(getClass());
    this.loggerFactory = lfactory ;
  }

  public void onInit() {
    logger.info("Start onInit()") ;
    ModuleRegistration.loadByAnnotation(availableModules, "com.neverwinterdp.server.module");
    ArrayList<String> moduleNames = new ArrayList<String>() ;
    for(ModuleRegistration sel : availableModules.values()) {
      if(sel.isAutoInstall()) moduleNames.add(sel.getModuleName()) ;
    }
    install(null, moduleNames.toArray(new String[moduleNames.size()])) ;
    logger.info("Finish onInit()");
  }
  
  public void onDestroy() {
    logger.info("Start onDestroy()");
    for(ServiceContainer sel : installedModules.values()) {
      sel.uninstall(parentContainer);
    }
    logger.info("Finish onDestroy()");
  }

  
  public ModuleRegistration[] install(Map<String, String> properties, String ...moduleNames)  {
    logger.info("Start install(String ... moduleNames)");
    List<ModuleRegistration> moduleStatusHolder = new ArrayList<> () ;
    for(int i = 0; i < moduleNames.length; i++) {
      if(installedModules.containsKey(moduleNames[i])) {
        logger.info("Module " + moduleNames[i] + " is already installed");
        continue ;
      }
      ModuleRegistration mreg = availableModules.get(moduleNames[i]) ;
      if(mreg == null) {
        logger.info("Module " + moduleNames[i] + " is not available");
        continue ;
      }
      try {
        Class<ServiceModule> clazz = (Class<ServiceModule>) Class.forName(mreg.getConfigureClass());
        ServiceModule module = clazz.newInstance() ;
        module.init(properties, runtimeEnvironment);
        ServiceContainer scontainer = parentContainer.getInstance(ServiceContainer.class) ;
        scontainer.init(mreg, module, loggerFactory);
        scontainer.install(parentContainer);
        installedModules.put(mreg.getModuleName(), scontainer) ;
        mreg.setInstallStatus(InstallStatus.INSTALLED);
        moduleStatusHolder.add(mreg) ;
      } catch(Exception ex) {
        logger.error("Cannot install the module " + moduleNames[i], ex);
      }
    }
    logger.info("Finish install(String ... moduleNames)");
    return moduleStatusHolder.toArray(new ModuleRegistration[moduleStatusHolder.size()]) ;
  }
  
  public ModuleRegistration[] uninstall(String ... moduleNames) throws Exception {
    logger.info("Start  uninstall(String ... moduleNames)");
    List<ModuleRegistration> holder = new ArrayList<ModuleRegistration>() ;
    for(int i = 0; i < moduleNames.length; i++) {
      ServiceContainer scontainer = installedModules.get(moduleNames[i]) ;
      if(scontainer != null) {
        installedModules.remove(moduleNames[i]) ;
        ModuleRegistration mstatus = scontainer.getModuleStatus() ;
        scontainer.stop(); 
        scontainer.uninstall(parentContainer);
        mstatus.setInstallStatus(InstallStatus.AVAILABLE);
        mstatus.setRunningStatus(RunningStatus.UNINSTALLED);
        holder.add(mstatus) ;
      } else {
        logger.warn("Cannot find the module " + moduleNames[i] + " to uninstall");
      }
    }
    logger.info("Finish uninstall(String ... moduleNames)");
    return holder.toArray(new ModuleRegistration[holder.size()]) ;
  }
  
  public void start() {
    logger.info("Start start()");
    for(ServiceContainer container : installedModules.values()) {
      if(container.getModuleStatus().isAutostart()) {
        container.start();
      }
    }
    logger.info("Finish start()");
  }

  public ModuleRegistration[] start(String ...moduleNames)  {
    logger.info("Start start(String ... moduleNames)");
    List<ModuleRegistration> moduleStatusHolder = new ArrayList<> () ;
    for(int i = 0; i < moduleNames.length; i++) {
      ServiceContainer scontainer = installedModules.get(moduleNames[i]) ;
      if(scontainer == null) {
        logger.info("Module " + moduleNames[i] + " is not installed");
        continue ;
      }
      scontainer.start() ; 
      ModuleRegistration mstatus = scontainer.getModuleStatus() ;
      moduleStatusHolder.add(mstatus) ;
    }
    logger.info("Finish start(String ... moduleNames)");
    return moduleStatusHolder.toArray(new ModuleRegistration[moduleStatusHolder.size()]) ;
  }
  
  public void stop() {
    logger.info("Start stop()");
    for(ServiceContainer container : installedModules.values()) {
      container.stop() ;
    }
    logger.info("Finish stop()");
  }

  public ModuleRegistration[] stop(String ...moduleNames)  {
    logger.info("Start stop(String ... moduleNames)");
    List<ModuleRegistration> moduleStatusHolder = new ArrayList<> () ;
    for(int i = 0; i < moduleNames.length; i++) {
      ServiceContainer scontainer = installedModules.get(moduleNames[i]) ;
      if(scontainer == null) {
        logger.info("Module " + moduleNames[i] + " is not installed");
        continue ;
      }
      scontainer.stop() ; 
      ModuleRegistration mstatus = scontainer.getModuleStatus() ;
      moduleStatusHolder.add(mstatus) ;
    }
    logger.info("Finish stop(String ... moduleNames)");
    return moduleStatusHolder.toArray(new ModuleRegistration[moduleStatusHolder.size()]) ;
  }
  
  public <T> T getInstance(String module, Class<T> type) {
    ServiceContainer container = installedModules.get(module) ;
    if(container != null) return container.getInstance(type);
    return null ;
  }
  
  public <T extends Service> T getService(String module, String serviceId) {
    ServiceContainer container = installedModules.get(module) ;
    if(container != null) return container.getService(serviceId);
    return null ;
  }
  
  public void start(ServiceRegistration registration) {
    ServiceContainer container = installedModules.get(registration.getModule()) ;
    if(container != null) container.start(registration);
  }

  public void stop(ServiceRegistration registration) {
    ServiceContainer container = installedModules.get(registration.getModule()) ;
    if(container != null) container.stop(registration);
  }

  public List<ServiceRegistration> getServiceRegistrations() {
    List<ServiceRegistration> holder = new ArrayList<ServiceRegistration>();
    for(ServiceContainer sel : installedModules.values()) {
      sel.collect(holder);
    }
    return holder;
  }

  /**
   * This method is used to find a specifice service by the service descriptor
   * 
   * @param registration
   * @return
   */
  public Service getService(ServiceRegistration registration) {
    ServiceContainer container = installedModules.get(registration.getModule()) ;
    if(container != null) return container.getService(registration);
    return null ;
  }
  
  public ModuleRegistration[] getAvailableModules() {
    return this.availableModules.values().toArray(new ModuleRegistration[availableModules.size()]) ;
  }
  
  public ModuleRegistration[] getInstalledModules() {
    ModuleRegistration[] array = new ModuleRegistration[installedModules.size()] ;
    int idx = 0 ;
    for(ServiceContainer sel : installedModules.values()) {
      array[idx++] = sel.getModuleStatus() ;
    }
    return array ;
  }
}
