package com.neverwinterdp.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.neverwinterdp.server.ModuleStatus.InstallStatus;
import com.neverwinterdp.server.service.Service;
import com.neverwinterdp.server.service.ServiceModule;
import com.neverwinterdp.server.service.ServiceRegistration;
import com.neverwinterdp.util.LoggerFactory;
import com.neverwinterdp.util.text.StringUtil;
/**
 * @author Tuan Nguyen
 * @email tuan08@gmail.com
 */
@Singleton
public class ModuleContainer {
  @Inject
  private Injector parentContainer ;
  
  @Inject(optional = true) @Named("server.available-modules")
  private String availableModuleClasses ;
  
  @Inject(optional = true) @Named("server.install-modules")
  private String installModuleClasses ;
  
  @Inject(optional = true) @Named("server.install-modules-autostart")
  private boolean installModuleAutostart = true ;
  
  private LoggerFactory loggerFactory ;
  private Logger          logger;
  
  private Map<String, ModuleStatus>    availableModules = new ConcurrentHashMap<String, ModuleStatus>();
  private Map<String, ServiceContainer> installedModules = new ConcurrentHashMap<String, ServiceContainer>();

  @Inject
  public ModuleContainer(LoggerFactory lfactory) throws Exception {
    logger = lfactory.getLogger(getClass());
    this.loggerFactory = lfactory ;
  }

  public void onInit() {
    logger.info("Start onInit()");
    String[] classNames = StringUtil.toStringArray(this.availableModuleClasses, ",") ;
    for(String className : classNames) {
      try {
        Class<ServiceModule> clazz = (Class<ServiceModule>) Class.forName(className);
        ServiceModule module = clazz.newInstance() ;
        ModuleStatus mstatus = new ModuleStatus() ;
        mstatus.setModuleName(module.getName());
        mstatus.setConfigureClass(className);
        mstatus.setInstallStatus(InstallStatus.AVAILABLE);
        availableModules.put(mstatus.getModuleName(), mstatus) ;
      } catch(Exception ex) {
        logger.error("Cannot instantiate module " + className, ex) ;
      }
    }
    Set<String> installModuleClass = StringUtil.toStringHashSet(this.installModuleClasses, ",") ;
    ArrayList<String> moduleNames = new ArrayList<String>() ;
    for(ModuleStatus module : availableModules.values()) {
      if(installModuleClass.contains(module.getConfigureClass())) {
        moduleNames.add(module.getModuleName()) ;
        installModuleClass.remove(module.getClass().getName()) ;
      }
    }
    for(String moduleClass : installModuleClass) {
      logger.warn("Cannot find the module class " + moduleClass + " in the available module classes to install") ;
    }
    install(moduleNames.toArray(new String[moduleNames.size()])) ;
    logger.info("Finish onInit()");
  }
  
  public void onDestroy() {
    logger.info("Start onDestroy()");
    for(ServiceContainer sel : installedModules.values()) {
      sel.uninstall(parentContainer);
    }
    logger.info("Finish onDestroy()");
  }

  
  public ModuleStatus[] install(String ...moduleNames)  {
    logger.info("Start install(String ... moduleNames)");
    List<ModuleStatus> moduleStatusHolder = new ArrayList<> () ;
    for(int i = 0; i < moduleNames.length; i++) {
      if(installedModules.containsKey(moduleNames[i])) {
        logger.info("Module " + moduleNames[i] + " is already installed");
        continue ;
      }
      ModuleStatus mstatus = availableModules.get(moduleNames[i]) ;
      if(mstatus == null) {
        logger.info("Module " + moduleNames[i] + " is not available");
        continue ;
      }
      try {
        Class<ServiceModule> clazz = (Class<ServiceModule>) Class.forName(mstatus.getConfigureClass());
        ServiceModule module = clazz.newInstance() ;

        ServiceContainer scontainer = parentContainer.getInstance(ServiceContainer.class) ;
        scontainer.init(mstatus, module, loggerFactory);
        scontainer.install(parentContainer);
        installedModules.put(scontainer.getModule().getName(), scontainer) ;
        mstatus.setInstallStatus(InstallStatus.INSTALLED);
        moduleStatusHolder.add(mstatus) ;
      } catch(Exception ex) {
        logger.error("Cannot install the module " + moduleNames[i]);
      }
    }
    logger.info("Finish install(String ... moduleNames)");
    return moduleStatusHolder.toArray(new ModuleStatus[moduleStatusHolder.size()]) ;
  }
  
  public ModuleStatus[] uninstall(String ... moduleNames) throws Exception {
    logger.info("Start  uninstall(String ... moduleNames)");
    List<ModuleStatus> holder = new ArrayList<ModuleStatus>() ;
    for(int i = 0; i < moduleNames.length; i++) {
      ServiceContainer scontainer = installedModules.get(moduleNames[i]) ;
      if(scontainer != null) {
        installedModules.remove(moduleNames[i]) ;
        ModuleStatus mstatus = scontainer.getModuleStatus() ;
        scontainer.stop(); 
        scontainer.uninstall(parentContainer);
        mstatus.setInstallStatus(InstallStatus.AVAILABLE);
        mstatus.setRunningStatus(null);
        holder.add(mstatus) ;
      } else {
        logger.warn("Cannot find the module " + moduleNames[i] + " to uninstall");
      }
    }
    logger.info("Finish uninstall(String ... moduleNames)");
    return holder.toArray(new ModuleStatus[holder.size()]) ;
  }
  
  public void start() {
    logger.info("Start start()");
    if(this.installModuleAutostart) {
      for(ServiceContainer container : installedModules.values()) {
        container.start();
      }
    }
    logger.info("Finish start()");
  }

  public ModuleStatus[] start(String ...moduleNames)  {
    logger.info("Start start(String ... moduleNames)");
    List<ModuleStatus> moduleStatusHolder = new ArrayList<> () ;
    for(int i = 0; i < moduleNames.length; i++) {
      ServiceContainer scontainer = installedModules.get(moduleNames[i]) ;
      if(scontainer == null) {
        logger.info("Module " + moduleNames[i] + " is not installed");
        continue ;
      }
      scontainer.start() ; 
      ModuleStatus mstatus = scontainer.getModuleStatus() ;
      moduleStatusHolder.add(mstatus) ;
    }
    logger.info("Finish start(String ... moduleNames)");
    return moduleStatusHolder.toArray(new ModuleStatus[moduleStatusHolder.size()]) ;
  }
  
  public void stop() {
    logger.info("Start stop()");
    for(ServiceContainer container : installedModules.values()) {
      container.stop() ;
    }
    logger.info("Finish stop()");
  }

  public ModuleStatus[] stop(String ...moduleNames)  {
    logger.info("Start stop(String ... moduleNames)");
    List<ModuleStatus> moduleStatusHolder = new ArrayList<> () ;
    for(int i = 0; i < moduleNames.length; i++) {
      ServiceContainer scontainer = installedModules.get(moduleNames[i]) ;
      if(scontainer == null) {
        logger.info("Module " + moduleNames[i] + " is not installed");
        continue ;
      }
      scontainer.stop() ; 
      ModuleStatus mstatus = scontainer.getModuleStatus() ;
      moduleStatusHolder.add(mstatus) ;
    }
    logger.info("Finish stop(String ... moduleNames)");
    return moduleStatusHolder.toArray(new ModuleStatus[moduleStatusHolder.size()]) ;
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
  
  public ModuleStatus[] getAvailableModules() {
    return this.availableModules.values().toArray(new ModuleStatus[availableModules.size()]) ;
  }
  
  public ModuleStatus[] getInstalledModules() {
    ModuleStatus[] array = new ModuleStatus[installedModules.size()] ;
    int idx = 0 ;
    for(ServiceContainer sel : installedModules.values()) {
      array[idx++] = sel.getModuleStatus() ;
    }
    return array ;
  }
}
