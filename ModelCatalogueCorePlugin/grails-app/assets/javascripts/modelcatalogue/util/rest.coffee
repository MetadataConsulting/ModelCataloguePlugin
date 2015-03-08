angular.module('mc.util.rest', ['mc.util.messages']).factory 'rest',  [ '$q', '$http', '$rootScope', ($q, $http, $rootScope) ->

  (config) ->
    deferred = $q.defer()

    $http(config).then(
      (response) ->
        config.statusListener(response.status) if config.statusListener?
        if !response.data and response.status >=200 and response.status < 300
          deferred.resolve response.status
        else if !response.data.errors?
          deferred.resolve response.data
        else
          deferred.reject response
    , (response) ->
      config.statusListener(response.status) if config.statusListener?
      if response.status is 0
        $rootScope.$broadcast 'applicationOffline', response
      if response.status is 404
        $rootScope.$broadcast 'resourceNotFound', response
      deferred.reject response
    , (update) ->
      deferred.update update
    )
    deferred.promise
]