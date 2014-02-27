# creates function which will return promise always resolving to given value
angular.module('mc.util.rest', []).factory 'rest', [ '$q', '$http', ($q, $http) ->
  (config, enhancer = (toBeEnhanced)-> toBeEnhanced) ->
    deferred = $q.defer()

    $http(config).then(
      (response) ->
        if !response.data and response.status >=200 and response.status < 300
          deferred.resolve response.status
        else if !response.data.errors?
          deferred.resolve enhancer(response.data)
        else
          deferred.reject response
    , (response) ->
      deferred.reject response
    )

    deferred.promise
]