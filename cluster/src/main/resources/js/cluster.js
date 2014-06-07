console = {
  print: function(text) { print(text) ; },

  println: function(text) { print(text + '\n') ; },

  printJSON: function(obj) { 
    this.println(JSON.stringify(obj, null, "  ")) ; 
  },

  printError: function(e) { 
    var stacktrace = e.stack || e.stacktrace || "";
    this.println(e.message) ; 
    this.println(stacktrace) ; 
  }
}

function Response(results) {
  this.results = results ;
  this.success = true ;

  this.isEmpty = function() { return this.results.length == 0 ; } ;

  this.assertSuccess = function() {
    Assert.assertTrue(resp.success && !resp.isEmpty()) ;
  };

  for(var i = 0; i < results.length; i++) {
    var result = results[i];
    if(result.error != null) {
      this.success = false ;
      break ;
    }
  }
}


cluster = {
  members: function() {
    var json = clusterAPI.getMembers() ;
    return JSON.parse(json) ;
  },

  clusterRegistration: function() {
    var json = clusterAPI.clusterRegistration() ;
    return JSON.parse(json) ;
  },

  server : {
    call: function(command, config) {
      if(config.params == null)  config.params = {} ; 
      config.params._commandName = command ;
      var json = clusterAPI.server.call(JSON.stringify(config.params)) ;
      var results = JSON.parse(json) ;
      if(config.onResponse) {
        config.onResponse(new Response(results));
      }
    },

    ping: function(config) { this.call('ping', config) ; },

    metric: function(config) { this.call('metric', config); },

    start: function(config) { this.call('start', config); },

    shutdown: function(config) { this.call('shutdown', config); },

    exit: function(config) { this.call('exit', config); }
  },

  module : {
    call: function(command, config) {
      if(config.params == null)  config.params = {} ; 
      config.params._commandName = command ;
      var json = clusterAPI.module.call(JSON.stringify(config.params)) ;
      var results = JSON.parse(json) ;
      if(config.onResponse) {
        config.onResponse(new Response(results));
      }
    },

    list: function(config) { this.call('list', config) ; },

    install: function(config) { this.call('install', config) ; },

    uninstall: function(config) { this.call('uninstall', config); }
  }
}
