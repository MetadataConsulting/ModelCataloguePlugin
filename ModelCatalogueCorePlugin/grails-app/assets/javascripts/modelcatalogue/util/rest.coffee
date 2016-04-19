angular.module('mc.util.rest', ['mc.util.messages']).factory 'rest', ($q, $http, $rootScope, $timeout) ->

  rest = (config) ->
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
        deferred.reject response
        return
      if response.status is 404
        if config.noRetry404
          $rootScope.$broadcast 'resourceNotFound', response, config
          deferred.reject response
          return

        retryNotFound = ->
          newConfig = angular.copy(config)
          newConfig.noRetry404 = true

          rest(newConfig).then (newResult) ->
            deferred.resolve newResult
          , (newResponse) ->
            deferred.reject newResponse

        $timeout retryNotFound, (if config.retryAfter then config.retryAfter else 1000)

        return

      deferred.reject response
    , (update) ->
      deferred.update update
    )
    deferred.promise

  rest

