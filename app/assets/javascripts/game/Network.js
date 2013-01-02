define([ "Class" ], function(Class) {
  "use strict";

  var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket;

  var Network = Class.extend({
    init: function(wsUrl, callback) {
      this.callback = callback;

      this.eventSocket = new WS(wsUrl);
      this.eventSocket.onmessage = this.receiveEvent(this);
    },

    sendMessage: function(obj) {
      console.log("Sending", obj);
      this.eventSocket.send(JSON.stringify(obj));
    },

    receiveEvent: function(self) {
      return function(event) {
        var data = JSON.parse(event.data);

        if (data.error) {
          eventSocket.close();
          console.log("ERROR");
          return;
        }

        self.callback(data);
      }
    }
  });

  return Network;
});