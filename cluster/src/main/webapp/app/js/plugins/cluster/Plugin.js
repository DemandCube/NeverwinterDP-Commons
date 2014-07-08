define([
  'plugins/cluster/UINavigation'
], function(UINavigation) {
  var Plugin = {
    name: "cluster",
    uiNavigation: new UINavigation()
  }

  return Plugin ;
});
