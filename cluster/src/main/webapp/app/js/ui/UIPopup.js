define([
  'jquery', 
  'underscore', 
  'backbone'
], function($, _, Backbone) {
  var UIPopup = Backbone.View.extend({
    el: "#UIPopupDialog",
    
    initialize: function (config) {
    },

    activate: function(uicomp, config) {
      uicomp.setElement($(this.el)).render();
      $(this.el).dialog(config);
    },
    
    closePopup: function() {
      $(this.el).dialog("close");
    }
  });
  
  return new UIPopup() ;
});
