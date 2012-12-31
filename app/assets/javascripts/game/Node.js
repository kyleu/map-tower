define([ "Class", "Theme" ], function(Class, Theme) {
  // Node
  var Node = Class.extend({
    init: function(n) {
      this.osmId = n.osmId;
      this.name = n.name;
      this.category = n.category;
      this.subcategory = n.subcategory;
      this.loc = n.loc;
      this.note = n.note;
    },

    render: function(mapView) {
      if (!this.marker) {
        this.marker = this.createMarker();
        mapView.nodeGroup.addLayer(this.marker);
      }
    },

    createMarker: function() {
      var circleOptions = {
        color: 'black',
        fillColor: Theme.getColor(this.category, this.subcategory),
        fillOpacity: 0.5
      };
      var ret = new L.CircleMarker(new L.LatLng(this.loc.y, this.loc.x), circleOptions);
      message = "lat: " + this.loc.y + "<br/>lng: " + this.loc.x;
      message += "<br/><br/>\n<strong>" + this.name + "</strong><br/>\n" + this.category + "<br/>\n" + this.subcategory + "<br/><br/>\n";
      message += this.note.split(",").join("<br/>\n");
      ret.bindPopup(message);
      return ret;
    }
  });
  return Node;
});