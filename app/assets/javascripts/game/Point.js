define([ "Class" ], function(Class) {
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
  return Point;
});