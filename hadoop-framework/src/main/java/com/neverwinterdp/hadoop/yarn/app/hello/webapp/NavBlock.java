package com.neverwinterdp.hadoop.yarn.app.hello.webapp;

import static org.apache.hadoop.mapreduce.v2.app.webapp.AMParams.RM_WEB;

import java.util.List;

import org.apache.hadoop.mapreduce.v2.api.records.AMInfo;
import org.apache.hadoop.mapreduce.v2.util.MRApps;
import org.apache.hadoop.mapreduce.v2.util.MRWebAppUtil;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet.DIV;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

import com.google.inject.Inject;

public class NavBlock extends HtmlBlock {
  final App app;

  @Inject NavBlock(App app) { this.app = app; }

  @Override protected void render(Block html) {
    String rmweb = $(RM_WEB);
    DIV<Hamlet> nav = html.
      div("#nav").
        h3("Cluster").
        ul().
          li().a(url(rmweb, "cluster", "cluster"), "About")._().
          li().a(url(rmweb, "cluster", "apps"), "Applications")._().
          li().a(url(rmweb, "cluster", "scheduler"), "Scheduler")._()._().
        h3("Application").
        ul().
          li().a(url("app/info"), "About")._().
          li().a(url("app"), "Jobs")._()._();
    nav.
      h3("Tools").
      ul().
        li().a("/conf", "Configuration")._().
        li().a("/logs", "Local logs")._().
        li().a("/stacks", "Server stacks")._().
        li().a("/metrics", "Server metrics")._()._()._();
  }
}
