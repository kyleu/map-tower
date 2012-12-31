require([ "game/Game", "ui/Panels" ], function(Game, Panels) {
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
