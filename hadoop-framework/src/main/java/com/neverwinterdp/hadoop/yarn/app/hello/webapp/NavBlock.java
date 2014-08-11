package com.neverwinterdp.hadoop.yarn.app.hello.webapp;

import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet.DIV;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

import com.google.inject.Inject;

public class NavBlock extends HtmlBlock {
  final App app;

  @Inject 
  NavBlock(App app) { 
    this.app = app; 
  }

  @Override protected void render(Block html) {
    DIV<Hamlet> nav = html.
      div("#nav").
        a(url("/"), "Index").
        span(" ").
        a(url("/info"), "Info").
        span(" ").
        a(url("../ws/info"), "Rest Info");
    nav._();
  }
}
