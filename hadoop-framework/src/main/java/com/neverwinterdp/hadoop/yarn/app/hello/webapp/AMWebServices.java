package com.neverwinterdp.hadoop.yarn.app.hello.webapp;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;

@Path("/ws")
public class AMWebServices {
  private final App app;

  private @Context HttpServletResponse response;
  
  @Inject
  public AMWebServices(AppContext context) {
    System.out.println("AMWebServices: application id = " + context.getApplicationId());
    this.app = new App(context);
  }
  
  @GET
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public App get() { 
    System.out.println("AMWebServices: get() " + app.getApplicationId());
    return app; 
  }

  @GET
  @Path("/info")
  @Produces({ MediaType.APPLICATION_JSON})
  public App getAppInfo() {
    System.out.println("AMWebServices: getAppInfo() " + app.getApplicationId());
    return app;
  }
}