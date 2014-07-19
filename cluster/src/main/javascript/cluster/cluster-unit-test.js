function testCluster() {
  var members = cluster.ClusterGateway.members() ;
  Assert.assertTrue(members.length > 0)
  console.printJSON(cluster.ClusterGateway.members()) ;
  var clusterRegistration = cluster.ClusterGateway.clusterRegistration() ;
  Assert.assertNotNull(clusterRegistration)
  //console.printJSON(clusterRegistration) ;
  var printer = new cluster.ClusterRegistrationPrinter(console, clusterRegistration);
  printer.printServerRegistration() ;
  printer.printServiceRegistration() ;
}

function testServer() {
  var member = cluster.ClusterGateway.members()[0] ;
  var memberIpPort = member.ipAddress + ":" +  member.port ;
  var memberUuid  = member.uuid ;

  cluster.ClusterGateway.execute({
    command: "server ping --member-role master" ,

    onResponse: function(resp) {
      new cluster.ResponsePrinter(console, resp).print();
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
      Assert.assertEquals("RUNNING", resp.results[0].result) ;
    }
  }) ;

  cluster.ClusterGateway.execute({
    command: "server metric --member-role master" ,

    onResponse: function(resp) {
      //console.printJSON(resp) ;
      var result = resp.results[0] ;
      var printer = new cluster.MetricPrinter(console, result.fromMember, result.result);
      printer.printCounter();
      printer.printTimer();
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
    }
  }) ;

  cluster.ClusterGateway.execute({
    command: "server metric-clear --member-role master --expression *Hello*" ,

    onResponse: function(resp) {
      console.h1("Remove *Hello* metric monitor") ;
      new cluster.ResponsePrinter(console, resp).print() ;
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
    }
  }) ;

  cluster.ClusterGateway.execute({
    command: "server shutdown --member-role master",

    onResponse: function(resp) {
      console.printJSON(resp) ;
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
      Assert.assertEquals("SHUTDOWN", resp.results[0].result) ;
    }
  }) ;

  cluster.ClusterGateway.execute({
    command: "server start --member-uuid " + memberUuid,

    onResponse: function(resp) {
      console.printJSON(resp) ;
      Assert.assertTrue(resp.success && !resp.isEmpty(), "Start server") ;
      Assert.assertEquals("RUNNING", resp.results[0].result) ;
    }
  }) ;
}

function testModule() {
  var member = cluster.ClusterGateway.members()[0] ;
  var memberIpPort = member.ipAddress + ":" +  member.port ;

  cluster.ClusterGateway.execute({
    command: "module list --member-role master --type available" ,

    onResponse: function(resp) {
      console.println("List the available modules") ;
      console.printJSON(resp) ;
      for(var i = 0; i < resp.results.length; i++) {
        var result = resp.results[i];
        var printer = new cluster.ModuleRegistrationPrinter(console, result.fromMember, result.result);
        printer.printModuleRegistration() ;
      }
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
    }
  }) ;

  cluster.ClusterGateway.execute({
    command: "module install " +
             "  --member-role master " +
             "  -Phello:install=from-install" +
             "  --autostart --module HelloModuleDisable",

    onResponse: function(resp) {
      console.println("Install module HelloModuleDisable") ;
      console.printJSON(resp) ;
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
    }
  }) ;

  cluster.ClusterGateway.execute({
    command: "module uninstall --member-role master --module HelloModuleDisable",

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
