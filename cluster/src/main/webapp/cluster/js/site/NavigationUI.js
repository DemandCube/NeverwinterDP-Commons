define([
  'jquery',
  'jqueryui',
  'underscore', 
  'backbone',
  'text!site/NavigationUI.jtpl'
], function($, jqueryui, _, Backbone, NavigationTmpl) {
  var NavigationUI = Backbone.View.extend({
    el: $("#NavigationUI"),
    
    initialize: function () {
      _.bindAll(this, 'render') ;
    },
    
    _template: _.template(NavigationTmpl),
    
    render: function() {
      var params = { 
      } ;
      $(this.el).html(this._template(params));
      $(this.el).trigger("create") ;
      $(this.el).find(".NavigationUIMenu").menu();
    },
    
    events: {
      'change select.onSelectLanguage': 'onSelectLanguage'
    },
    
    onSelectLanguage: function(evt) {
    }
  });
  
  return NavigationUI ;
});
