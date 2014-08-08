package com.neverwinterdp.hadoop.yarn.app.hello.webapp;

import static org.apache.hadoop.yarn.util.StringHelper.join;

import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.webapp.Controller;

import com.google.inject.Inject;

public class AppController extends Controller  {
  static final String RM_WEB = "rm.web";
  static final String APP_ID = "app.id";
  
  protected final App app ;
  
  protected AppController(App app, Configuration conf, RequestContext ctx, String title) {
    super(ctx);
    this.app = app;
    set(APP_ID, app.context.getApplicationId());
  }
  
  @Inject
  protected AppController(App app, Configuration conf, RequestContext ctx) {
    this(app, conf, ctx, "am");
  }
  
  /**
   * Render the default(index.html) page for the Application Controller
   */
  @Override public void index() {
    setTitle(join("Hello App Controller ", $(APP_ID)));
  }
  
  /**
   * Render the /info page with an overview of current application.
   */
  public void info() {
    info("Application Master Overview").
      _("Application ID:", "App Id").
      _("Application Name:", "App Name").
      _("User:", "App User").
      _("Started on:", new Date()).
      _("Elasped: ", "1000ms");
    render(InfoPage.class);
  }
}
