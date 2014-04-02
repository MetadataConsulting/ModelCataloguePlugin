angular.module('mc.util.rest', ['mc.util.enhance']).factory 'rest',  [ '$q', '$http', ($q, $http) ->

  (config) ->
    deferred = $q.defer()

    $http(config).then(
      (response) ->
        if !response.data and response.status >=200 and response.status < 300
          deferred.resolve response.status
        else if !response.data.errors?
          deferred.resolve response.data
        else
          deferred.reject response
    , (response) ->
      deferred.reject response
    , (update) ->
      deferred.update update
    )
    deferred.promise
]