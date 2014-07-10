define([
  'jquery',
  'underscore', 
  'backbone',
  'service/ClusterGateway',
  'ui/UIBreadcumbs',
  'site/UIWorkspace',
  'plugins/cluster/UIListServer',
  'plugins/cluster/UIServerInfo',
  'text!plugins/cluster/UINavigation.jtpl'
], function($, _, Backbone, ClusterGateway, UIBreadcumbs, UIWorkspace, UIListServer, UIServerInfo, Template) {
  var UINavigation = Backbone.View.extend({

    initialize: function () {
      _.bindAll(this, 'render', 'onListServer', 'onServerInfo') ;
    },
    
    _template: _.template(Template),
    
    render: function() {
      var creg = ClusterGateway.getClusterRegistration() ;
      var params = { 
        clusterRegistration: ClusterGateway.getClusterRegistration()
      } ;
      $(this.el).html(this._template(params));
      $(this.el).find(".UINavigationMenu").menu();
    },

    events: {
      'click .onListServer': 'onListServer',
      'click .onServerInfo': 'onServerInfo'
    },
    
    onListServer: function(evt) {
      this._workspace(new UIListServer()) ;
    },

    onServerInfo: function(evt) {
      this._workspace(new UIServerInfo()) ;
    },

    _workspace: function(uicomp) {
      var uiContainer = new UIBreadcumbs({el: null}) ;
      UIWorkspace.setUIComponent(uiContainer) ;
      uiContainer.add(uicomp) ;
    }
  });

  return UINavigation ;
});
