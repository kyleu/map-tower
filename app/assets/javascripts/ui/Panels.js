define([ "game/Game" ], function(Game) {
  var numGameEvents = 0;

  var onGameEvent = function(msg) {
    var handled = true;
    if(msg.kind == "talk") {
      logToTalk(msg.kind, msg.user, msg.data);
    } else if(msg.kind == "join") {
      logToTalk(msg.kind, msg.user, msg.data);
    } else {
      handled = false;
    }
    $("#member-list").text("Players: " + msg.members.join(", "));
    $("#network-stats").text(++numGameEvents + " events processed.");
    return handled;
  }

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

  var logToTalk = function(kind, user, data) {
    var el = $('<div class="message"><span></span><p></p></div>');
    $("span", el).text(user);
    $("p", el).text(data);
    $(el).addClass(kind);
    if (data.user == '@username') {
      $(el).addClass('me');
    }
    $('#messages').append(el);
  }

  var handleReturnKey = function(e) {
    if (e.charCode == 13 || e.keyCode == 13) {
      e.preventDefault();
      Game.send({
        type: "Chat",
        chat: $("#talk").val()
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
  
  return {
    gameCallback: onGameEvent
  };
});