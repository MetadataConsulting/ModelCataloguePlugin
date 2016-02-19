//= wrapped

(function(window, angular) {
    var headers = {};

    angular.module("mc.util.MessagingClient", []).provider("MessagingClient", MessagingClientProvider);

    function MessagingClient(Stomp, SockJS, SockJSURL, $q, $log) {
        var subscriptions = {}, StompClient = Stomp.over(new SockJS(SockJSURL));

        this.isConnected = function () {
            return StompClient.ws.readyState == 1;
        };

        this.connect = function (force) {
            var deferred = $q.defer();

            if (!force && this.isConnected()) {
                return $q.when({});
            }

            StompClient.connect(headers, function (frame) {
                $log.debug(frame);
                deferred.resolve(frame)
            }, function (err) {
                $log.error(err);
                deferred.reject(err)
            });

            return deferred.promise;
        };

        this.disconnect = function () {
            var deferred = $q.defer();
            StompClient.disconnect(function () {
                deferred.resolve();
            });
            return deferred.promise;
        };

        this.subscribe = function (destination, callback, headers) {
            var deferred = $q.defer(), subscription;
            if (!this.isConnected()) {
                this.connect().then(function () {
                    subscription = StompClient.subscribe(destination, callback, headers);
                    subscriptions[destination] = subscription;
                    deferred.resolve(subscription);
                }, function () {
                    deferred.reject();
                });
                return deferred.promise
            }
            subscription = StompClient.subscribe(destination, callback, headers);
            subscriptions[destination] = subscription;
            deferred.resolve(subscription);
            return deferred.promise;
        };

        this.unsubscribe = function(destination) {
            var subscription = subscriptions[destination];
            if (subscription) {
                subscription.unsubscribe();
                return $q.when(true);
            }
            return $q.when(false);
        };

        this.send = function (destination, headers, body) {
            var deferred = $q.defer();

            if (!this.isConnected()) {
                this.connect().then(function () {
                    StompClient.send(destination, headers, body);
                    deferred.resolve(true);
                }, function () {
                    deferred.reject();
                });
                return deferred.promise
            }
            StompClient.send(destination, headers, body);
            deferred.resolve(true);
            return deferred.promise;
        };

        // to assign the handlers
        this.connect(true);

        return this;
    }

    function MessagingClientFactory(Stomp, SockJS, SockJSURL, $q, $log) {
        return new MessagingClient(Stomp, SockJS, SockJSURL, $q, $log);
    }

    function MessagingClientProvider() {

        this.setHeaders = function (config) {
            headers = config || {};
        };

        this.$get = MessagingClientFactory;
    }
})(window, angular);