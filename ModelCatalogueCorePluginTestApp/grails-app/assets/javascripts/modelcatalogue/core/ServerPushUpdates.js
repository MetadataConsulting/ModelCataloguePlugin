(function(window, angular){

    angular.module('mc.core.serverPushUpdates', ['mc.util.MessagingClient']).factory('serverPushUpdates', function(MessagingClient, $rootScope, security){

        var destToSub = {}, destToStatus = {};

        function addSubscriptionInternal(destination, callback) {
            var subscriptions = destToSub[destination] ? destToSub[destination] : [];

            subscriptions.push(callback);

            destToSub[destination] = subscriptions;

            if (!destToStatus[destination]) {
                subscribeToDestination(destination);
            }
        }

        function subscribeToDestination(destination) {
            MessagingClient.subscribe(destination, function(message){
                var payload = JSON.parse(message.body), element, subscriptions = destToSub[destination];

                element = JSON.parse(payload.element);

                angular.forEach(subscriptions ? subscriptions : [], function(callback){
                    callback(element, payload.change);
                });

            });
            // subscribed
            destToStatus[destination] = true
        }

        function subscribe() {
            for (var destination in destToSub) {
                if (destToSub.hasOwnProperty(destination)) {
                    if (!destToStatus[destination]) {
                        subscribeToDestination(destination)
                    }
                }
            }
        }

        if (security.isUserLoggedIn()) {
            subscribe();
        }

        $rootScope.$on('userLoggedIn', function() {
            MessagingClient.disconnect().then(function(){
                subscribe();
            });

        });

        $rootScope.$on('userLoggedOut', function() {
            angular.forEach();
            MessagingClient.disconnect();
        });

        this.subscribe = function(destination, callback) {
            addSubscriptionInternal(destination, callback);
        };

        return this

    });

})(window, angular);
