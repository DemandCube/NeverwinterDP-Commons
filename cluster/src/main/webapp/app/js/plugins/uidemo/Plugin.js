define([
  'plugins/uidemo/UINavigation'
], function(UINavigation) {
  var Plugin = {
    name: "uidemo",
    uiNavigation: new UINavigation()
  }

  return Plugin ;
});
