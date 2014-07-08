define([
  'jquery',
  'underscore', 
  'backbone',
  'service/ClusterGateway',
  'text!plugins/cluster/UINavigation.jtpl'
], function($, _, Backbone, ClusterGateway, Template) {
  var UINavigation = Backbone.View.extend({

    initialize: function () {
      _.bindAll(this, 'render') ;
    },
    
    _template: _.template(Template),
    
    render: function() {
      var creg = ClusterGateway.getClusterRegistration() ;
      var params = { 
        clusterRegistration: ClusterGateway.getClusterRegistration()
      } ;
      $(this.el).html(this._template(params));
      $(this.el).trigger("create") ;
      $(this.el).find(".UINavigationMenu").menu();
    }
  });

  return UINavigation ;
});
