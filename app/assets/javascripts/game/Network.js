define([ "Class" ], function(Class) {
  var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket;

  var Network = Class.extend({
    init: function(wsUrl, callback) {
      this.callback = callback;

      var eventSocket = new WS(wsUrl);
      eventSocket.onmessage = this.receiveEvent;
    },

    sendMessage: function(obj) {
      console.log("Sending", obj);
      eventSocket.send(JSON.stringify(obj));
    },

    receiveEvent: function(event) {
      var data = JSON.parse(event.data);
      console.log("Received", event);

      if (data.error) {
        eventSocket.close();
        console.log("ERROR");
        return;
      }

      var el = $('<div class="message"><span></span><p></p></div>');
      $("span", el).text(data.user);
      $("p", el).text(data.message);
      $(el).addClass(data.kind);
      if (data.user == '@username') {
        $(el).addClass('me');
      }
      $('#messages').append(el);
    }
  });
  
  
  return Network;
});