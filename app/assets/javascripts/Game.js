require([ "jquery", "game/MapTower", "ui/MapView", "ui/Panels" ], function($, MapTower) {
  $(function() {
    $(function() {
      MapTower.init(window.gameType);
      MapTower.initView("map");
    });
  });
});
