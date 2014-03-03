# creates function which will return promise always resolving to given value
angular.module('mc.util.createConstantPromise', []).factory 'createConstantPromise', [ '$q', ($q) ->
  (value) ->
    deferred = $q.defer()
    deferred.resolve(value)
    deferred.promise
]