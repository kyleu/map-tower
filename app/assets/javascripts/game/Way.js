define([ "Class", "Util" ], function(Class, Util) {
  var Way = Class.extend({
    init : function(w) {
      this.name = w.name;
      this.category = w.category;
      this.subcategory = w.subcategory;
      this.note = w.note;

      this.latlngs = new Array();
      for ( var i in w.points) {
        this.latlngs.push(new L.LatLng(w.points[i].y, w.points[i].x));
      }
    },

    render : function(mapView) {
      if (!this.way) {
        this.way = this.createWay();
        mapView.wayGroup.addLayer(this.way);
      }
    },

    createWay : function() {
      var ret = new L.Polyline(this.latlngs, {
        color : Util.getColor(this.category, this.subcategory)
      });
      message = "Way (" + this.latlngs.length + " points)<br/><br/>\n<strong>" + this.name + "</strong><br/>\n" + this.category + "<br/>\n" + this.subcategory + "<br/><br/>\n";
      message += this.note.split(",").join("<br/>\n");
      ret.bindPopup(message);
      return ret;
    }
  });
  return Way;
});
