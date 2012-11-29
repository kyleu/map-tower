define([ "game/Point", "game/Node", "game/Way", "ui/MapView" ], function(Point, Node, Way, MapView) {
  // Handles server communication, game state, input, animation, other shizzle. Should break this class up soon.
  var MapTower = {
    init: function(gameType) {
      MapTower.gameType = gameType;
      MapTower.gameType.initialCenter = new Point(MapTower.gameType.initialCenter.x, MapTower.gameType.initialCenter.y);

      MapTower.nodeCache = {};
      MapTower.wayCache = {};
      MapTower.mapView = null;
    },

    clear: function() {
      // clear, remove nodes/ways, reset game
    },

    initView: function(divId) {
      MapTower.mapView = new MapView(divId, MapTower.gameType.initialCenter, MapTower.gameType.initialZoom);
      MapTower.update(MapTower.mapView.map.getBounds());
      MapTower.mapView.addTileLayer();
    },

    networkCallback: function(rsp) {
      for ( var i in rsp.nodes) {
        MapTower.addNode(rsp.nodes[i]);
      }
      for ( var i in rsp.ways) {
        MapTower.addWay(rsp.ways[i]);
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
      $.get('/game/' + this.gameType.code + '/data', params, MapTower.networkCallback, "json");
    },

    addNode: function(obj) {
      if (MapTower.nodeCache[obj.osmId]) {
        console.warn("Encountered cached node on update: ", obj);
      } else {
        node = new Node(obj);
        MapTower.nodeCache[node.osmId] = node;
        node.render(MapTower.mapView);
      }
    },

    addWay: function(obj) {
      if (this.wayCache[obj.osmId]) {
        console.warn("Encountered cached way on update: ", obj);
      } else {
        var way = new Way(obj);
        MapTower.wayCache[way.osmId] = way;
        way.render(MapTower.mapView);
      }
    }
  };
  return MapTower;
});
