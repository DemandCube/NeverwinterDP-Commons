define([
  'jquery',
  'underscore', 
  'backbone',
  'text!site/WorkspaceUI.jtpl'
], function($, _, Backbone, WorkspaceTmpl) {
  var WorkspaceUI = Backbone.View.extend({
    el: $("#WorkspaceUI"),
    
    initialize: function () {
      _.bindAll(this, 'render') ;
    },
    
    _template: _.template(WorkspaceTmpl),
    
    render: function() {
      var params = { 
      } ;
      $(this.el).html(this._template(params));
      $("#tabs").tabs();
    },
    
    events: {
      'change select.onSelectLanguage': 'onSelectLanguage'
    },
    
    onSelectLanguage: function(evt) {
    }
  });
  
  return WorkspaceUI ;
});
