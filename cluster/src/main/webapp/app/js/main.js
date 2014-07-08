var ROOT_CONTEXT = window.location.pathname.substring(0, window.location.pathname.lastIndexOf("/"));

var libs = "../../js/libs" ;

require.config({
  urlArgs: "bust=" + (new Date()).getTime(), //prevent cache for development
  baseUrl: 'js',
  waitSeconds: 60,
  
  paths: {
    libs:         libs,
    jquery:       libs + '/jquery/jquery',
    jqueryui:     libs + '/jquery/jquery-ui-1.11.0/jquery-ui',
    underscore:   libs + '/underscore/underscore-1.5.2',
    backbone:     libs + '/backbonejs/backbonejs-1.1.0',
  },
  
  shim: {
    jquery: {
      exports: '$'
    },
    jqueryui: {
      deps: ["jquery"],
      exports: "jqueryui"
    },
    underscore: {
      exports: '_'
    },
    backbone: {
      deps: ["underscore", "jquery"],
      exports: "Backbone"
    }
  }
});

require([
  'jquery', 'app'
], function($, App){
  app = App ;
  app.initialize() ;
});
