define([ "Class", "game/Point" ], function(Class, Point) {
  "use strict";

  var Mob = Class.extend({
    init: function(m) {
      this.id = m.id
      this.name = m.name;
      this.loc = new Point(m.x, m.y);
      console.log(this);
    }
  });
  return Mob;
});