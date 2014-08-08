package com.neverwinterdp.hadoop.yarn.app.hello.webapp;

import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.view.InfoBlock;

public class InfoPage extends AppView {

  @Override protected void preHead(Page.HTML<_> html) {
    commonPreHead(html);
    setTitle("About the Application Master");
  }

  @Override protected Class<? extends SubView> content() {
    return InfoBlock.class;
  }
}
