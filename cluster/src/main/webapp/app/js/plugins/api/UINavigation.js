define([
  'jquery',
  'underscore', 
  'backbone',
  'service/ClusterGateway',
  'text!plugins/api/UINavigation.jtpl'
], function($, _, Backbone, ClusterGateway, Template) {
  var UINavigation = Backbone.View.extend({

    initialize: function () {
      _.bindAll(this, 'render') ;
    },
    
    _template: _.template(Template),
    
    render: function() {
      var params = { 
      } ;
      $(this.el).html(this._template(params));
      $(this.el).trigger("create") ;
    }
  });

  return UINavigation ;
});
