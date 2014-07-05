define([
  'jquery',
  'underscore', 
  'backbone',
  'text!site/FooterUI.jtpl'
], function($, _, Backbone, FooterTmpl) {
  var FooterUI = Backbone.View.extend({
    el: $("#FooterUI"),
    
    initialize: function () {
      _.bindAll(this, 'render') ;
    },
    
    _template: _.template(FooterTmpl),
    
    render: function() {
      var params = { 
      } ;
      $(this.el).html(this._template(params));
      $(this.el).trigger("create") ;
    },
    
    events: {
      'change select.onSelectLanguage': 'onSelectLanguage'
    },
    
    onSelectLanguage: function(evt) {
    }
  });
  
  return FooterUI ;
});
