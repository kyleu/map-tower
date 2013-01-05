define([ "Class", "game/Network", "game/Point", "game/Node", "game/Way", "game/Mob", "ui/MapView" ], function(Class, Network, Point, Node, Way, Mob, MapView) {
  "use strict";

  var Game = Class.extend({
    init: function() {
      this.nodeCache = {};
      this.wayCache = {};
      this.mobCache = {};
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
      this.nodeCache = {};
      this.wayCache = {};
      this.mobCache = {};
      this.mapView.clear();
    },

    initView: function(divId) {
      this.mapView = new MapView(divId, this.gameType.initialCenter, this.gameType.initialZoom);
      this.eventCallbacks.push(_.bind(this.mapView.onGameEvent, this.mapView));

      this.loadMapData(this.mapView.map.getBounds());
      this.mapView.addTileLayer();
    },

    send: function(data) {
      this.network.sendMessage(data);
    },

    onEvent: function(msg) {
      var shouldProcess = this.updateGame(msg);
      if(shouldProcess) {
        var results = _.map(this.eventCallbacks, function(c){ return c(msg); });
        if(!_.some(results)) {
          console.log("Unhandled message of type \"" + msg.kind + "\".", msg);
        }
      } else {
        console.log("Cancelled message of type \"" + msg.kind + "\".", msg);
      }
    },

    // return value indicates if event should propagate to other handlers.
    updateGame: function(msg) {
      var ret = true;
      if(msg.kind == "spawn") {
        console.log("Spawning " + msg.user + " at " + msg.data.x + ", " + msg.data.y + ".");
        //$("body").append("<img class=\"mob\" src=\"/assets/images/mob/slime.png\" />")
      }
      return ret;
    },

    loadMapData: function(b) {
      var ul = b.getNorthWest();
      var br = b.getSouthEast();
      var params = {
        "min.x": ul.lng,
        "min.y": br.lat,
        "max.x": br.lng,
        "max.y": ul.lat
      };

      $.get('/game/' + this.gameType.code + '/data', params, _.bind(this.mapDataCallback, this), "json");
    },

    mapDataCallback: function(rsp) {
      for ( var i in rsp.nodes) {
        this.addNode(rsp.nodes[i]);
      }
      for ( var i in rsp.ways) {
        this.addWay(rsp.ways[i]);
      }
    },

    addNode: function(obj) {
      if (this.nodeCache[obj.id]) {
        console.warn("Encountered cached node on update: ", obj);
      } else {
        var node = new Node(obj);
        this.nodeCache[node.id] = node;
        node.render(this.mapView);
      }
    },

    addWay: function(obj) {
      if (this.wayCache[obj.id]) {
        console.warn("Encountered cached way on update: ", obj);
      } else {
        var way = new Way(obj);
        this.wayCache[way.id] = way;
        way.render(this.mapView);
      }
    },

    addMob: function(obj) {
      if (this.mobCache[obj.id]) {
        console.warn("Encountered cached mob on update: ", obj);
      } else {
        var mob = new Mob(obj);
        this.mobCache[mob.id] = way;
        mob.render(this.mapView);
      }
    }
  });

  var activeGame = new Game();
  return activeGame;
});
