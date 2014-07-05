define([
  'jquery',
  'service/Server',
  'site/BannerUI',
  'site/FooterUI',
  'site/NavigationUI',
  'site/WorkspaceUI',
], function($, Server, BannerUI, FooterUI, NavigationUI, WorkspaceUI) {
  //var response = Server.clusterRequest("server", "ping", {} );
  console.log("loading app!!!!") ;
  var app = {
    view : {
      BannerUI: new BannerUI(),
      NavigationUI: new NavigationUI(),
      WorkspaceUI: new WorkspaceUI(),
      FooterUI: new FooterUI(),
    },

    initialize: function() {
      console.log("start initialize app") ;
      this.render() ;
      console.log("finish initialize app") ;
    },

    render: function() {
      this.view.BannerUI.render() ;
      this.view.NavigationUI.render() ;
      this.view.WorkspaceUI.render() ;
      this.view.FooterUI.render() ;
    },

    reload: function() {
      window.location = ROOT_CONTEXT + "/index.html" ;
    }
  } ;
  
  return app ;
});
