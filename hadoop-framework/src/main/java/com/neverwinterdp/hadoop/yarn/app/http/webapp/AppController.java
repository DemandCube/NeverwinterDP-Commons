package com.neverwinterdp.hadoop.yarn.app.http.webapp;

import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.webapp.Controller;

import com.google.inject.Inject;
import com.neverwinterdp.hadoop.yarn.app.http.webapp.html.IndexPage;
import com.neverwinterdp.hadoop.yarn.app.http.webapp.html.MonitorPage;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;

public class AppController extends Controller  {
  
  protected AppController(AppMaster appMaster, Configuration conf, RequestContext ctx, String title) {
    super(ctx);
  }
  
  @Inject
  protected AppController(AppMaster appMaster, Configuration conf, RequestContext ctx) {
    this(appMaster, conf, ctx, "am");
    System.out.println("AppController: application id = " + appMaster.getConfig().appId);
  }
  
  /**
   * Render the default(index.html) page for the Application Controller
   */
  @Override 
  public void index() {
    //renderText("Hello world!"); 
    //setTitle(join("Hello App Controller ", $(APP_ID)));
    //new Exception().printStackTrace();
    try {
      render(IndexPage.class);
    } catch(Throwable t) {
      renderText("Error: " + t.getMessage()) ;
      t.printStackTrace();
    }
  }
  
  /**
   * Render the /monitor page with an overview of current application.
   */
  public void monitor() {
    render(MonitorPage.class);
  }
}
