require([ "jquery", "game/Game", "ui/Panels" ], function($, Game) {
  $(function() {
    $(function() {
      Game.init(window.gameType);
    });
  });
});
