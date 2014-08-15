package com.neverwinterdp.hadoop.yarn.app.webapp.html;

import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet.HTML;
import org.apache.hadoop.yarn.webapp.view.HtmlPage;

abstract public class AbstractHtmlPage extends HtmlPage {
  protected void render(HTML<_> html) {
    html.
      title().
        _("Hello Page").
      _() ; 
    html.
      body().
        div().
          $id("nav").
        _().
        div().
          $id("content").
        _().
      _().
    _() ;
    render(nav());
    render(content());
  }
  
  abstract protected Class<? extends SubView> nav() ;

  abstract protected Class<? extends SubView> content() ;
}