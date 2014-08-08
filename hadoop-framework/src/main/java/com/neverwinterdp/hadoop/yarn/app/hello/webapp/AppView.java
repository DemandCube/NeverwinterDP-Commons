package com.neverwinterdp.hadoop.yarn.app.hello.webapp;

import static org.apache.hadoop.yarn.webapp.view.JQueryUI.ACCORDION;
import static org.apache.hadoop.yarn.webapp.view.JQueryUI.ACCORDION_ID;
import static org.apache.hadoop.yarn.webapp.view.JQueryUI.DATATABLES;
import static org.apache.hadoop.yarn.webapp.view.JQueryUI.DATATABLES_ID;
import static org.apache.hadoop.yarn.webapp.view.JQueryUI.initID;
import static org.apache.hadoop.yarn.webapp.view.JQueryUI.tableInit;

import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.view.TwoColumnLayout;

public class AppView extends TwoColumnLayout {

  @Override protected void preHead(Page.HTML<_> html) {
    commonPreHead(html);
    set(DATATABLES_ID, "jobs");
    set(initID(DATATABLES, "jobs"), jobsTableInit());
    setTableStyles(html, "jobs");
  }

  protected void commonPreHead(Page.HTML<_> html) {
    set(ACCORDION_ID, "nav");
    set(initID(ACCORDION, "nav"), "{autoHeight:false, active:1}");
  }

  @Override
  protected Class<? extends SubView> nav() {
    return NavBlock.class;
  }

  @Override
  protected Class<? extends SubView> content() {
    return JobsBlock.class;
  }

  private String jobsTableInit() {
    return tableInit().
        // Sort by id upon page load
        append(", aaSorting: [[0, 'asc']]").
        append(",aoColumns:[{sType:'title-numeric'},").
        append("null,null,{sType:'title-numeric', bSearchable:false},null,").
        append("null,{sType:'title-numeric',bSearchable:false}, null, null]}").
        toString();
  }
}
