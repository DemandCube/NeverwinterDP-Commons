package com.neverwinterdp.hadoop.yarn.app.hello.webapp;

import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet.DIV;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

import com.google.inject.Inject;

public class IndexPage extends AbstractHtmlPage {

  protected Class<? extends SubView> nav() {
    return NavBlock.class ;
  }

  protected Class<? extends SubView> content() {
    return ContentBlock.class ;
  }
  
  static public class ContentBlock extends HtmlBlock {
    AppContext appContext;

    @Inject 
    ContentBlock(AppContext appContext) { 
      this.appContext = appContext ; 
    }

    @Override protected void render(Block html) {
      html.div()._("Index Page Content, app id = ", appContext.getApplicationId())._();
    }
  }
}