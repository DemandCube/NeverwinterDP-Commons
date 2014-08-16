package com.neverwinterdp.hadoop.yarn.app.http.webapp.html;

import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

import com.google.inject.Inject;
import com.neverwinterdp.hadoop.yarn.app.AppConfig;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;

public class IndexPage extends AbstractHtmlPage {

  protected Class<? extends SubView> nav() {
    return NavBlock.class ;
  }

  protected Class<? extends SubView> content() {
    return ContentBlock.class ;
  }
  
  static public class ContentBlock extends HtmlBlock {
    AppMaster appMaster ;

    @Inject 
    ContentBlock(AppMaster appMaster ) { 
      this.appMaster = appMaster; 
    }

    @Override protected void render(Block html) {
      AppConfig config = appMaster.getConfig() ;
      String[][] data = {
          {"App Id", config.appId},
          {"App Home", config.appHome},
          {"App Name", config.appName},
          {"App Max Memory", config.appMaxMemory + ""},
          {"App Max Number Of Worker", config.appNumOfWorkers + ""},
          {"App Container Manager",   config.appContainerManager},
          {"App Hostname", config.appHostName},
          {"App RPC Port", config.appRpcPort + ""},
          {"App Tracking URL", config.appTrackingUrl},
          {"Worker CLass", config.worker},
          {"Worker Number Of Cores", config.workerNumOfCore + ""},
          {"Worker Max Memory", config.workerMaxMemory + ""},
      };
      for(int i = 0; i < data.length; i++) {
        String[] entry = data[i] ;
        html.
          div().
            _(entry[0],":", entry[1]).
          _();
      }
    }
  }
}