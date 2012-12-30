define([ "Class", "game/Network", "game/Point", "game/Node", "game/Way", "game/Mob", "ui/MapView" ], function(Class, Network, Point, Node, Way, Mob, MapView) {
  var mapDataCallback = function(self) {
    return function(rsp) {
      for ( var i in rsp.nodes) {
        self.addNode(rsp.nodes[i]);
      }
      for ( var i in rsp.ways) {
        self.addWay(rsp.ways[i]);
      }
    };
  };

  var Game = Class.extend({
    init: function() {
      this.nodeCache = {};
      this.wayCache = {};
      this.mapView = null;
    },

    start: function(gameType, eventCallbacks) {
      this.eventCallbacks = eventCallbacks
      this.gameType = gameType;
      this.gameType.initialCenter = new Point(this.gameType.initialCenter.x, this.gameType.initialCenter.y);

      this.initView("map");
      var networkCallback = _.bind(this.onEvent, this);
      this.network = new Network(gameType.websocketUrl, networkCallback);
    },

    clear: function() {
      // clear, remove nodes/ways, reset game
    },

    initView: function(divId) {
      this.mapView = new MapView(divId, this.gameType.initialCenter, this.gameType.initialZoom);
      this.eventCallbacks.push(_.bind(this.mapView.onGameEvent, this.mapView));

      this.update(this.mapView.map.getBounds());
      this.mapView.addTileLayer();
    },

    sendEvent: function(data) {
      this.network.sendMessage(data);
    },

    onEvent: function(msg) {
      var results = _.map(this.eventCallbacks, function(c){ return c(msg); });
      if(!_.some(results)) {
        console.log("Unhandled message of type \"" + msg.kind + "\".", msg);
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

      $.get('/game/' + this.gameType.code + '/data', params, mapDataCallback(this), "json");
    },

    addNode: function(obj) {
      if (this.nodeCache[obj.osmId]) {
        console.warn("Encountered cached node on update: ", obj);
      } else {
        node = new Node(obj);
        this.nodeCache[node.osmId] = node;
        node.render(this.mapView);
      }
    },

    addWay: function(obj) {
      if (this.wayCache[obj.osmId]) {
        console.warn("Encountered cached way on update: ", obj);
      } else {
        var way = new Way(obj);
        this.wayCache[way.osmId] = way;
        way.render(this.mapView);
      }
    }
  });

  return new Game();
});
