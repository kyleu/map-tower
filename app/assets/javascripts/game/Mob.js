define([ "Class", "game/Point" ], function(Class, Point) {
  var Mob = Class.extend({
    init: function(name, x, y) {
      this.name = name;
      this.loc = new Point(x, y);
      console.log(this);
    }
  });
  return Mob;
});