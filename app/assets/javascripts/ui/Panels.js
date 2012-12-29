define([ "game/Game" ], function(Game) {
  var onPanelEvent = function(msg, opt) {
    var mv = Game.mapView;
    if(msg == "map" && opt == "zoom-in") {
      mv.zoomIn();
    } else if(msg == "map" && opt == "zoom-out") {
      mv.zoomOut();
    } else if(msg == "draw-way"){
      opt ? mv.map.addLayer(mv.wayGroup) : mv.map.removeLayer(mv.wayGroup);
    } else if(msg == "draw-node"){
      opt ? mv.map.addLayer(mv.nodeGroup) : mv.map.removeLayer(mv.nodeGroup);
    } else if(msg == "draw-bg"){
      opt ? mv.map.addLayer(mv.tileLayer) : mv.map.removeLayer(mv.tileLayer);
    } else if(msg == "debug"){
      onDebugEvent(opt);
    } else {
      console.log("Unhandled message " + msg + "(" + opt + ").");
    }
  };

  var onDebugEvent = function(opt) {
    if(opt == "reload") {
      console.log("reload");
    } else {
      console.log("Unhandled debug message \"" + opt + "\".");
    }
  };

  var handleReturnKey = function(e) {
    if (e.charCode == 13 || e.keyCode == 13) {
      e.preventDefault();
      Game.network.sendMessage({
        text : $("#talk").val()
      })
      $("#talk").val('');
    }
  };

  $(function() {
    $(".panel button").click(function(e) {
      onPanelEvent(e.srcElement.name, e.srcElement.value);
    });
    $(".panel input:radio").click(function(e) {
      onPanelEvent(e.srcElement.name, e.srcElement.value);
    });
    $(".panel input:checkbox").click(function(e) {
      onPanelEvent(e.srcElement.name, e.srcElement.checked);
    });
    $("#talk").keypress(handleReturnKey);
  });
});