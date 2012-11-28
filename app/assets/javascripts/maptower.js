var initializing = false;
var fnTest = /xyz/.test(function(){xyz;}) ? /\b_super\b/ : /.*/;

Class = function() {};

Class.extend = function(prop) {
  var _super = this.prototype;

  initializing = true;
  var prototype = new this();
  initializing = false;
  
  for (var name in prop) {
    var exists = (typeof prop[name] == "function" && typeof _super[name] == "function" && fnTest.test(prop[name]))
    prototype[name] = exists ? (function(name, fn){
      return function() {
        var tmp = this._super;
        this._super = _super[name];
        var ret = fn.apply(this, arguments);
        this._super = tmp;
        return ret;
      };
    })(name, prop[name]) : prop[name];
  }

  Class = function () {
    if ( !initializing && this.init ) {
      this.init.apply(this, arguments);
    }
  }

  Class.prototype = prototype;

  // Enforce the constructor to be what we expect
  Class.constructor = Class;

  // And make this class extendable
  Class.extend = arguments.callee;

  return Class;
};

var Util = {
  getColor: function(cat, subcat) {
    var key = cat + ":" + subcat;
    var color = Util.nodeColors[key];
    if(color == null) {
      color = Util.randomColor();
      Util.nodeColors[key] = color;
    }
    return color;
  },

  randomColor: function() {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.round(Math.random() * 15)];
    }
    return color;
  },

  nodeColors: {
    
  }
}

// Point - x and y are lng and lat doubles.
var Point = Class.extend({
  init: function(x, y) {
    this.x = x;
    this.y = y;
  },
  latLng: function() {
    return new L.LatLng(this.y, this.x);
  }
});

// Node
var Node = Class.extend({
  init: function(n) {
    this.name = n.name;
    this.category = n.category;
    this.subcategory = n.subcategory;
    this.loc = n.loc;
    this.note = n.note;
  },

  render: function(mapView) {
    if(!this.marker) {
      this.marker = this.createMarker();
      mapView.nodeGroup.addLayer(this.marker);
    }
  },

  createMarker: function() {
    var circleOptions = {
      color: 'black',
      fillColor: Util.getColor(this.category, this.subcategory),
      fillOpacity: 0.5
    };
    var ret = new L.CircleMarker(new L.LatLng(this.loc.y, this.loc.x), circleOptions);
    message = "lat: " + this.loc.y + "<br/>lng: " + this.loc.x + "<br/><br/>\n<strong>" + this.name + "</strong><br/>\n" + this.category + "<br/>\n" + this.subcategory + "<br/><br/>\n";
    message += this.note.split(",").join("<br/>\n");
    ret.bindPopup(message);
    return ret;
  }
});

// Way
var Way = Class.extend({
  init: function(w) {
    this.name = w.name;
    this.category = w.category;
    this.subcategory = w.subcategory;
    this.note = w.note;

    this.latlngs = new Array();
    for(var i in w.points) {
      this.latlngs.push(new L.LatLng(w.points[i].y, w.points[i].x));
    }
  },

  render: function(mapView) {
    if(!this.way) {
      this.way = this.createWay();
      mapView.wayGroup.addLayer(this.way);
    }
  },

  createWay: function() {
    var ret = new L.Polyline(this.latlngs, {color: Util.getColor(this.category, this.subcategory)});
    message = "Way (" + this.latlngs.length + " points)<br/><br/>\n<strong>" + this.name + "</strong><br/>\n" + this.category + "<br/>\n" + this.subcategory + "<br/><br/>\n";
    message += this.note.split(",").join("<br/>\n");
    ret.bindPopup(message);
    return ret;
  }
});

// Handles server communication, game state, input, animation, other shizzle. Should break this class up soon.
var MT = {
  init: function(gameType) {
    MT.gameType = gameType;
    MT.gameType.initialCenter = new Point(MT.gameType.initialCenter.x, MT.gameType.initialCenter.y);

    MT.nodeCache = {};
    MT.wayCache = {};
    MT.mapView = null;
  },

  clear: function() {
    // clear, remove nodes/ways, reset game
  },

  initView: function(divId) {
    MT.mapView = new MapView(divId, MT.gameType.initialCenter, MT.gameType.initialZoom);
    MT.update(MT.mapView.map.getBounds());
    MT.mapView.addTileLayer();
  },

  networkCallback: function(rsp) {
    for(var i in rsp.nodes) {
      MT.addNode(rsp.nodes[i]);
    }
    for(var i in rsp.ways) {
      MT.addWay(rsp.ways[i]); 
    }
  },

  update: function(b) {
    var ul = b.getNorthWest();
    var br = b.getSouthEast();
    params = {"min.x": ul.lng, "min.y": br.lat, "max.x": br.lng, "max.y": ul.lat};
    $.get('/game/' + this.gameType.code + '/data', params, MT.networkCallback, "json");
  },

  addNode: function(obj) {
    if(MT.nodeCache[obj.osmId]) {
      console.warn("Encountered cached node on update: ", obj);
    } else {
      node = new Node(obj);
      MT.nodeCache[node.osmId] = node;
      node.render(MT.mapView);
    }
  },

  addWay: function(obj) {
    if(this.wayCache[obj.osmId]) {
      console.warn("Encountered cached way on update: ", obj);
    } else {
      var way = new Way(obj);
      MT.wayCache[way.osmId] = way;
      way.render(MT.mapView);
    }
  }
};

// Contains all Leaflet interactions, caches map data
var MapView = Class.extend({
  init: function(id, center, zoom) {
    this.map = new L.Map('map', { attributionControl: false, zoomControl: false });
    this.map.setView(center.latLng(), zoom);
    this.map.on('click', this.onMapClick);
    this.map.on('zoomend', this.onMapZoom);

    this.nodeGroup = new L.LayerGroup();
    this.map.addLayer(this.nodeGroup);

    this.wayGroup = new L.LayerGroup();
    this.map.addLayer(this.wayGroup);
  },

  zoomIn: function() { 
    this.map.zoomIn(); 
  },

  zoomOut: function() {
    this.map.zoomOut();
  },

  addTileLayer: function() {
    // tileUrl = 'http://{s}.tile.cloudmade.com/0320d0049e1a4242bab7857cec8b343a/998/256/{z}/{x}/{y}.png'
    var tileUrl = '/tiles/{z}/{x}/{y}';
    this.tileLayer = new L.TileLayer(tileUrl, {
      maxZoom : 18
    });
    this.map.addLayer(this.tileLayer);
  },

  onMapClick: function(e) {
    var latlngStr = 'lat: ' + e.latlng.lat.toFixed(4) + '<br/>lng: ' + e.latlng.lng.toFixed(4);
    var popup = new L.Popup();
    popup.setLatLng(e.latlng);
    popup.setContent(latlngStr);
    this.openPopup(popup);
  },

  onMapZoom: function(e) { 
    // MapTower.update(this.map.getBounds())
  },
  
  hide: function() {
    console.log(this);
  }
});

function CustomIcon(iconUrl) {
  return L.Icon.extend({
    options: {
      iconUrl: iconUrl,
      shadowUrl: '/assets/images/map/shadow.png',
      iconSize: new L.Point(38, 95),
      shadowSize: new L.Point(68, 95),
      iconAnchor: new L.Point(22, 94),
      popupAnchor: new L.Point(-3, -76)
    }
  });
}

var pointIcon = CustomIcon('/assets/images/map/point.png');
var pointTagsIcon = CustomIcon('/assets/images/map/point-tags.png');
var wayIcon = CustomIcon('/assets/images/map/way.png');

// Game panels
var onEvent = function(msg, opt) {
  var mv = MT.mapView;
  if(msg == "map" && opt == "zoom-in") {
    mv.zoomIn();
  } else if(msg == "map" && opt == "zoom-out") {
    mv.zoomOut();
  } else if(msg == "draw-way"){
    opt ? mv.map.addLayer(mv.wayGroup) : mv.map.removeLayer(mv.wayGroup);
  } else if(msg == "draw-node"){
    opt ? mv.map.addLayer(mv.nodeGroup) : mv.map.removeLayer(mv.nodeGroup);
  } else if(msg == "draw-bg"){
    opt ? mv.map.addLayer(mv.tileLayer) : mv.map.removeLayer(mv.tileLayer);
  } else {
    console.log("Unhandled message " + msg + "(" + opt + ").");
  }
};

$(function() {
  MT.init(window.gameType);
  MT.initView("map");

  $(".panel button").click(function(e) {
    onEvent(e.srcElement.name, e.srcElement.value);
  });
  $(".panel input:radio").click(function(e) {
    onEvent(e.srcElement.name, e.srcElement.value);
  });
  $(".panel input:checkbox").click(function(e) {
    onEvent(e.srcElement.name, e.srcElement.checked);
  });

});
