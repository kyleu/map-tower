define([ "game/Network", "game/Point", "game/Node", "game/Way", "game/Mob", "ui/MapView" ], function(Network, Point, Node, Way, Mob, MapView) {
  // Handles server communication, game state, input, animation, other shizzle. Should break this class up soon.
  var Game = {
    init: function(gameType) {
      Game.gameType = gameType;
      Game.gameType.initialCenter = new Point(Game.gameType.initialCenter.x, Game.gameType.initialCenter.y);

      Game.nodeCache = {};
      Game.wayCache = {};
      Game.mapView = null;

      Game.initView("map");

      this.network = new Network(gameType.websocketUrl, Game.onEvent);
    },

    clear: function() {
      // clear, remove nodes/ways, reset game
    },

    initView: function(divId) {
      Game.mapView = new MapView(divId, Game.gameType.initialCenter, Game.gameType.initialZoom);
      Game.update(Game.mapView.map.getBounds());
      Game.mapView.addTileLayer();
    },

    onEvent: function() {

    },

    networkCallback: function(rsp) {
      for ( var i in rsp.nodes) {
        Game.addNode(rsp.nodes[i]);
      }
      for ( var i in rsp.ways) {
        Game.addWay(rsp.ways[i]);
      }
    },

    update: function(b) {
      var ul = b.getNorthWest();
      var br = b.getSouthEast();
      params = {
        "min.x": ul.lng,
        "min.y": br.lat,
        "max.x": br.lng,
        "max.y": ul.lat
      };
      $.get('/game/' + this.gameType.code + '/data', params, Game.networkCallback, "json");
    },

    addNode: function(obj) {
      if (Game.nodeCache[obj.osmId]) {
        console.warn("Encountered cached node on update: ", obj);
      } else {
        node = new Node(obj);
        Game.nodeCache[node.osmId] = node;
        node.render(Game.mapView);
      }
    },

    addWay: function(obj) {
      if (this.wayCache[obj.osmId]) {
        console.warn("Encountered cached way on update: ", obj);
      } else {
        var way = new Way(obj);
        Game.wayCache[way.osmId] = way;
        way.render(Game.mapView);
      }
    }
  };
  return Game;
});
