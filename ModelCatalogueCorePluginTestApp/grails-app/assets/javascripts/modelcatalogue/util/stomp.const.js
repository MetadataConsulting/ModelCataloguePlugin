(function(window){
    angular.module("mc.util.Stomp", [])
        .constant('SockJSURL', window.SockJSURL)
        .constant('Stomp', window.Stomp)
        .constant('SockJS', window.SockJS)
})(window);


