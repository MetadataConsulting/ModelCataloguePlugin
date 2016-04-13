(function(window){
    angular.module("mc.util.Saxon", []).factory('SaxonPromise', [ '$q', '$window',
        function($q, $window) {
            var SaxonPromise;
            if ($window.Saxon) {
                SaxonPromise = $q.when($window.Saxon);
                return SaxonPromise
            }
            SaxonPromise = $q(function(resolve){
                $window.onSaxonLoad = function() {
                    resolve($window.Saxon);
                }
            });
            return SaxonPromise;
        }
    ])
})(window);
