define([ "Class", "Theme" ], function(Class, Theme) {
  var onWayClick = function(e) {
    console.log(message, e);
  }

  var Way = Class.extend({
    init: function(w) {
      this.osmId = w.osmId;
      this.name = w.name;
      this.category = w.category;
      this.subcategory = w.subcategory;
      this.note = w.note;

      this.latlngs = new Array();
      for ( var i in w.points) {
        this.latlngs.push(new L.LatLng(w.points[i].y, w.points[i].x));
      }
    },

    render: function(mapView) {
      if (!this.polyline) {
        this.polyline = this.createPolyline();
        mapView.wayGroup.addLayer(this.polyline);
      }
    },

    createPolyline: function() {
      var ret = new L.Polyline(this.latlngs, {
        color: Theme.getColor(this.category, this.subcategory)
      });
      ret.way = this;

      message = "Way (" + this.latlngs.length + " points)<br/><br/>\n<strong>" + this.name + "</strong><br/>\n" + this.category + "<br/>\n" + this.subcategory
          + "<br/><br/>\n";
      message += this.note.split(",").join("<br/>\n");

      ret.on('click', onWayClick);

      // ret.bindPopup(message);
      return ret;
    }
  });
  return Way;
});
