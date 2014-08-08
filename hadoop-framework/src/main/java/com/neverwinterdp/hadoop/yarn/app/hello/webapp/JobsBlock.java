package com.neverwinterdp.hadoop.yarn.app.hello.webapp;

import static org.apache.hadoop.yarn.util.StringHelper.join;
import static org.apache.hadoop.yarn.webapp.view.JQueryUI._PROGRESSBAR;
import static org.apache.hadoop.yarn.webapp.view.JQueryUI._PROGRESSBAR_VALUE;

import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.app.webapp.dao.JobInfo;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet.TABLE;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet.TBODY;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

import com.google.inject.Inject;

public class JobsBlock extends HtmlBlock {
  final AppContext appContext;

  @Inject JobsBlock(AppContext appCtx) {
    appContext = appCtx;
  }

  @Override protected void render(Block html) {
    TBODY<TABLE<Hamlet>> tbody = html.
      h2("Active Jobs").
      table("#jobs").
        thead().
          tr().
            th(".id", "Job ID").
            th(".name", "Name").
            th(".state", "State").
            th("Map Progress").
            th("Maps Total").
            th("Maps Completed").
            th("Reduce Progress").
            th("Reduces Total").
            th("Reduces Completed")._()._().
        tbody();
    tbody._()._();
  }
}
