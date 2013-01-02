define([ "Class" ], function(Class) {
  "use strict";

  function randomColor() {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for ( var i = 0; i < 6; i++) {
      color += letters[Math.round(Math.random() * 15)];
    }
    return color;
  }

  var activeTheme = "standard";

  var nodeColors = {
    standard: {
      fallback: "#000",
      highway_secondary: "#00c",
      highway_tertiary: "#00a",
      highway: "#00f"
    },
    random: {},
    dark: {
      fallback: "#ddd"
    },
    sat: {
      fallback: "#000"
    }
  };

  var Theme = {
    setActiveTheme: function(theme) {
      if(activeTheme != theme) {
        activeTheme = theme;
        console.log("Changing theme to " + theme + ".");
      }
    },

    getActiveTheme: function() {
      return activeTheme;
    },

    getActiveNodeColors: function() {
      return nodeColors[activeTheme];
    },

    getColor: function(cat, subcat) {
      var key = cat + "_" + subcat;
      var colors = Theme.getActiveNodeColors();
      var color = colors[key];
      if (color == null) {
        color = colors[cat]
        if (color == null) {
          color = colors.fallback
        }
        if (color == null) {
          color = randomColor();
        }
        colors[key] = color;
      }
      return color;
    }
  };

  return Theme;
});