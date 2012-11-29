define([ "Class" ], function(Class) {
  var Util = {
    getColor: function(cat, subcat) {
      var key = cat + ":" + subcat;
      var color = Util.nodeColors[key];
      if (color == null) {
        color = Util.randomColor();
        Util.nodeColors[key] = color;
      }
      return color;
    },

    randomColor: function() {
      var letters = '0123456789ABCDEF'.split('');
      var color = '#';
      for ( var i = 0; i < 6; i++) {
        color += letters[Math.round(Math.random() * 15)];
      }
      return color;
    },

    nodeColors: {

    }
  };
  return Util;
});