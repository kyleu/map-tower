define([ "Class" ], function(Class) {
  // Contains all Leaflet interactions, caches map data
  var MapView = Class.extend({
    init: function(id, center, zoom) {
      this.map = new L.Map('map', {
        attributionControl: false,
        zoomControl: false
      });
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
        maxZoom: 18
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

  return MapView;
});