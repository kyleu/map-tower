require([ "jquery", "game/Game", "ui/Panels" ], function($, Game, Panels) {
  $(function() {
    $(function() {
      Game.start(window.gameType);
    });
  });
});
