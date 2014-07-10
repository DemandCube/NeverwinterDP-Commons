define([
  'jquery',
  'jqueryui',
  'underscore', 
  'backbone',
  'plugins/PluginManager',
], function($, jqueryui, _, Backbone, PluginManager) {
  var UINavigation = Backbone.View.extend({
    el: $("#UINavigation"),
    
    initialize: function () {
      _.bindAll(this, 'render') ;
    },
    

    _blockTmpl: _.template("<div style='padding: 10px' class='<%=name%>'></div>"),
    
    render: function() {
      var plugins = PluginManager.getPlugins() ;
      for(var i = 0; i < plugins.length; i++) {
        var plugin = plugins[i] ;
        if(plugin.uiNavigation == null) continue ;
        var cssClassName = 'Plugin' + plugin.name ;
        $(this.el).append(this._blockTmpl({name: cssClassName}));
        plugin.uiNavigation.setElement(this.$('.' + cssClassName)).render();
      }
    },
    
    events: {
      'change select.onSelectLanguage': 'onSelectLanguage'
    },
    
    onSelectLanguage: function(evt) {
    }
  });
  
  return UINavigation ;
});
