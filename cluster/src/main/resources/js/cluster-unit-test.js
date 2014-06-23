function testCluster() {
  var members = cluster.members() ;
  Assert.assertTrue(members.length > 0)
  console.printJSON(cluster.members()) ;
  var clusterRegistration = cluster.clusterRegistration() ;
  Assert.assertNotNull(clusterRegistration)
  //console.printJSON(clusterRegistration) ;
  var printer = new ClusterRegistrationPrinter(console, clusterRegistration);
  printer.printServerRegistration() ;
  printer.printServiceRegistration() ;
}

function testServer() {
  var member = cluster.members()[0] ;
  var ipPort = member.ipAddress + ":" +  member.port ;

  cluster.server.ping({
    params: { "member-role": "master" },

    onResponse: function(resp) {
      new ResponsePrinter(console, resp).print();
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
      Assert.assertEquals("RUNNING", resp.results[0].result) ;
    }
  }) ;

  cluster.server.metric({
    params: { "member-role": "master" },

    onResponse: function(resp) {
      //console.printJSON(resp) ;
      var result = resp.results[0] ;
      var printer = new MetricPrinter(console, result.fromMember, result.result);
      printer.printCounter();
      printer.printTimer();
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
    }
  }) ;

  cluster.server.clearMetric({
    params: { "member-role": "master", "expression": "*Hello*" },

    onResponse: function(resp) {
      console.h1("Remove *Hello* metric monitor") ;
      new ResponsePrinter(console, resp).print() ;
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
    }
  }) ;

  cluster.server.shutdown({
    params: { "member-role": "master" },

    onResponse: function(resp) {
      console.printJSON(resp) ;
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
      Assert.assertEquals("SHUTDOWN", resp.results[0].result) ;
    }
  }) ;

  cluster.server.start({
    params: { "member": ipPort },

    onResponse: function(resp) {
      console.printJSON(resp) ;
      Assert.assertTrue(resp.success && !resp.isEmpty(), "Start server") ;
      Assert.assertEquals("RUNNING", resp.results[0].result) ;
    }
  }) ;
}

function testModule() {
  var member = cluster.members()[0] ;
  var memberIpPort = member.ipAddress + ":" +  member.port ;

  cluster.module.list({
    params: { "member-role": "master", "type": "available" },

    onResponse: function(resp) {
      console.println("List the available modules") ;
      for(var i = 0; i < resp.results.length; i++) {
        var result = resp.results[i];
        var printer = new ModuleRegistrationPrinter(console, result.fromMember, result.result);
        printer.printModuleRegistration() ;
      }
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
    }
  }) ;

  cluster.module.install({
    params: { 
      "member": memberIpPort,  
      "autostart": true,
      "module": ["HelloModuleDisable"],
      "-Phello:install": "from-install" 
    },

    onResponse: function(resp) {
      console.println("Install module HelloModuleDisable") ;
      console.printJSON(resp) ;
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
    }
  }) ;

  cluster.module.uninstall({
    params: { 
      "member": memberIpPort,  
      "module": ["HelloModuleDisable"]
    },

    onResponse: function(resp) {
      console.println("Uninstall module HelloModuleDisable") ;
      console.printJSON(resp) ;
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
    }
  }) ;
}

try {
  testCluster() ;
  testServer() ;
  testModule() ;
} catch(error) {
  console.printError(error) ;
  throw error ;
}
