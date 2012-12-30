require.config({
  shim: {
    underscore: {
      exports: '_'
    }
  }
});

require([ "jquery", "extern/underscore", "game/Game", "ui/Panels" ], function($, _, Game, Panels) {
  $(function() {
    $(function() {
      Game.start(window.gameType, [Panels.gameCallback]);
    });
  });
});
