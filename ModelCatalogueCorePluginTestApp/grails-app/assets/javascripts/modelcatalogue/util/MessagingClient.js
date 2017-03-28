//= wrapped

(function(window, angular) {
    var headers = {};

    angular.module("mc.util.MessagingClient", ['mc.util.Stomp']).provider("MessagingClient", MessagingClientProvider);

    function MessagingClient(Stomp, SockJS, SockJSURL, $q, $interval, $log, $rootScope) {
        var subscriptions = {}, StompClient;

        this.isConnected = function () {
            return StompClient && StompClient.ws.readyState == 1;
        };

        this.connect = function (force) {
            var deferred = $q.defer(), intervalPromise, self = this;

            if (!force && this.isConnected()) {
                return $q.when({});
            }

            if (!StompClient) {
                var sockJS = new SockJS(SockJSURL);
                StompClient = Stomp.over(sockJS);
                StompClient.debug = function(message){
                    // uncomment to show stomp debug messages
                    // $log.debug(message);
                };
                sockJS.onclose = function() {
                  $rootScope.$$disconnected = true;
                  $rootScope.$broadcast('redrawContextualActions');
                  $rootScope.$broadcast('messagingClientDisconnected');
                };
            }

            StompClient.connect(headers, function () {}, function (err) {
                $log.error(err);
                if (intervalPromise) {
                    $interval.cancel(intervalPromise)
                }
                deferred.reject(err);
                $rootScope.$$disconnected = true;
                $rootScope.$broadcast('redrawContextualActions');
                $rootScope.$broadcast('messagingClientDisconnected');
            });

            if (this.isConnected()) {
                deferred.resolve();
            } else {
                intervalPromise = $interval(function(){
                    if (self.isConnected()) {
                        deferred.resolve();
                        $interval.cancel(intervalPromise);
                    }
                }, 100)
            }

            return deferred.promise;
        };

        this.disconnect = function () {
            var deferred = $q.defer();
            if (!StompClient) {
                return $q.when({});
            }
            StompClient.disconnect(function () {
                deferred.resolve();
            });
            StompClient = undefined;
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

        this._getStompClient = function() {
            return StompClient;
        };

        // to assign the handlers
        this.connect(true);

        return this;
    }

    function MessagingClientFactory(Stomp, SockJS, SockJSURL, $q, $interval, $log, $rootScope) {
        return new MessagingClient(Stomp, SockJS, SockJSURL, $q, $interval, $log, $rootScope);
    }

    function MessagingClientProvider() {

        this.setHeaders = function (config) {
            headers = config || {};
        };

        this.$get = MessagingClientFactory;
    }
})(window, angular);
