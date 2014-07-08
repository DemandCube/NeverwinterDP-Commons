define([
  'plugins/api/UINavigation'
], function(UINavigation) {
  var Plugin = {
    name: "api",
    uiNavigation: new UINavigation()
  }

  return Plugin ;
});
