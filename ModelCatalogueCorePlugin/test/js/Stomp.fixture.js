
(function(window){

  function MockStompClient() {

    sendPayload = undefined;

    this.ws = {readyState: 1};

    this.getSendPayload = function() {
      return sendPayload;
    };

    this.connect = function(headers, callback) {
      this.ws.readyState = 1;
      callback(headers)
    };

    this.disconnect = function(callback) {
      this.ws.readyState = 3;
      callback();
    };

    this.subscribe = function(destination, callback, headers) {
      var subscription = {
        destination: destination,
        headers: headers,
        body: JSON.stringify({foo: 'bar', element: '{}'})
      };
      callback(subscription);
      return subscription;
    };

    this.send = function (destination, headers, body) {
      sendPayload = {destination: destination, headers: headers, body: body}
    }

  }

  function MockSockJs() {}

  function MockStomp() {
    this.over = function(socket) {
      return new MockStompClient();
    };
  }

  window.SockJS = MockSockJs;
  window.Stomp = new MockStomp();
  window.SockJSURL = '/sockjs';

})(window);

