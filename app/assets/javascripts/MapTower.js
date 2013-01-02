require([ "game/Game", "ui/Panels" ], function(Game, Panels) {
  "use strict";

  $(function() {
    $(function() {
      Game.start(window.gameType, [Panels.gameCallback]);
    });

    window.debug = {
      game: Game,
      panels: Panels
    };
  });
});
