define(function() {
  var initializing = false;
  var fnTest = /xyz/.test(function() {xyz;}) ? /\b_super\b/ : /.*/;

  Class = function() {
  };

  Class.extend = function(prop) {
    var _super = this.prototype;

    initializing = true;
    var prototype = new this();
    initializing = false;

    for ( var name in prop) {
      var exists = (typeof prop[name] == "function" && typeof _super[name] == "function" && fnTest.test(prop[name]))
      prototype[name] = exists ? (function(name, fn) {
        return function() {
          var tmp = this._super;
          this._super = _super[name];
          var ret = fn.apply(this, arguments);
          this._super = tmp;
          return ret;
        };
      })(name, prop[name]) : prop[name];
    }

    Class = function() {
      if (!initializing && this.init) {
        this.init.apply(this, arguments);
      }
    }

    Class.prototype = prototype;

    // Enforce the constructor to be what we expect
    Class.constructor = Class;

    // And make this class extendable
    Class.extend = arguments.callee;

    return Class;
  };

  return Class;
});