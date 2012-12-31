define([ "Class" ], function(Class) {
  function randomColor() {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for ( var i = 0; i < 6; i++) {
      color += letters[Math.round(Math.random() * 15)];
    }
    return color;
  }

  var Theme = {
    activeTheme: "random",

    setActiveTheme: function(theme) {
      Theme.activeTheme = theme;
      console.log("Changing theme to " + theme + ".");
    },

    getColor: function(cat, subcat) {
      var key = cat + ":" + subcat;
      var color = Theme.nodeColors[Theme.activeTheme][key];
      if (color == null) {
        color = Theme.nodeColors[Theme.activeTheme].fallback
        if (color == null) {
          color = randomColor();
        }
        Theme.nodeColors[key] = color;
      }
      return color;
    },

    nodeColors: {
      standard: {
        fallback: "#000"
      },
      random: {},
      dark: {
        fallback: "#ddd"
      },
      sat: {
        fallback: "#000"
      }
    }
  };

  return Theme;
});