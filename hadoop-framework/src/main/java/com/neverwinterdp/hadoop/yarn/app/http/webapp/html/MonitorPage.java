package com.neverwinterdp.hadoop.yarn.app.http.webapp.html;

import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

import com.google.inject.Inject;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.util.JSONSerializer;

public class MonitorPage extends AbstractHtmlPage {

  protected Class<? extends SubView> nav() {
    return NavBlock.class ;
  }

  protected Class<? extends SubView> content() {
    return ContentBlock.class ;
  }
  
  static public class ContentBlock extends HtmlBlock {
    AppMaster appMaster ;

    @Inject 
    ContentBlock(AppMaster appMaster) { 
      this.appMaster = appMaster ; 
    }

    @Override protected void render(Block html) {
      html.h2("Monitor Data") ;
      html.
        pre().
          _(JSONSerializer.INSTANCE.toString(appMaster.getAppMonitor())).
        _() ;
    }
  }
}