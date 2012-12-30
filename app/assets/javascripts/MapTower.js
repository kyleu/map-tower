require.config({
  shim: {
    underscore: {
      exports: '_'
    }
  }
});

require([ "game/Game", "ui/Panels" ], function(Game, Panels) {
  $(function() {
    $(function() {
      Game.start(window.gameType, [Panels.gameCallback]);
    });
  });
});
