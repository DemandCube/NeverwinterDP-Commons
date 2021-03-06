package com.neverwinterdp.hadoop.yarn.app.http.webapp;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.neverwinterdp.hadoop.yarn.app.AppInfo;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;

@Path("/ws")
public class AMWebServices {
  private final App app;

  private @Context HttpServletResponse response;
  private AppMaster appMaster ;
  
  @Inject
  public AMWebServices(AppMaster appMaster) {
    this.appMaster = appMaster ;
    System.out.println("AMWebServices: application id = " + appMaster.getAppConfig().appId);
    this.app = new App(appMaster.getAppConfig().appId);
  }
  
  @GET
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public App get() { 
    System.out.println("AMWebServices: get() " + app.getApplicationId());
    return app; 
  }

  @GET
  @Path("/monitor")
  @Produces({ MediaType.APPLICATION_JSON})
  public AppInfo getAppInfo() {
    return appMaster.getAppInfo() ;
  }
}