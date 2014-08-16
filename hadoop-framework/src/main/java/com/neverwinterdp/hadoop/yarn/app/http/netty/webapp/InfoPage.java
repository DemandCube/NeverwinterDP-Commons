package com.neverwinterdp.hadoop.yarn.app.http.netty.webapp;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.github.mustachejava.MustacheFactory;
import com.neverwinterdp.netty.http.webapp.HtmlBlock;
import com.neverwinterdp.netty.http.webapp.HtmlPage;

public class InfoPage extends HtmlPage {
  private NavigationBlock navBlock ;
  private AppConfigBlock  appConfigBlock ;
  private HtmlBlock       bodyBlock ;
  
  public InfoPage() throws IOException {
    super("info", "classpath:webapps/info.mtpl");
    navBlock = new NavigationBlock(mFactory) ;
    appConfigBlock = new AppConfigBlock(mFactory) ;
    bodyBlock = appConfigBlock ;
  }

  public void render(Writer writer, Map<String, Object> scopes) throws Exception {
    scopes.put("navigation", navBlock.toHtml(scopes)) ;
    scopes.put("body",       bodyBlock.toHtml(scopes)) ;
    renderHtmlPage(writer, scopes);
  }

  static public class NavigationBlock extends HtmlBlock {
    public NavigationBlock(MustacheFactory mf) throws IOException {
      super("navigation", "classpath:webapps/navigation.mtpl", mf);
    }
  }
  
  static public class AppConfigBlock extends HtmlBlock {
    public AppConfigBlock(MustacheFactory mf) throws IOException {
      super("AppConfig", "classpath:webapps/app-config.mtpl", mf);
    }
  }
}

