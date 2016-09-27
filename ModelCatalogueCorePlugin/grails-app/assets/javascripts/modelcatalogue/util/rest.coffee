angular.module('mc.util.rest', ['mc.util.messages']).factory 'rest', ($q, $http, $rootScope, $timeout) ->

  staleTolerance = 100 # do not request again within this period (ms)
  pendingRequests = {}

  createKey = (config) ->
    method = config.method ? 'GET'
    url = config.url
    params = config.params ? {}

    return "#{method}:#{url}?#{"#{k}=#{v}" for k,v of params}"

  rest = (config) ->
    key = createKey(config)
    if (not config.method or config.method is 'GET') and pendingRequests.hasOwnProperty(key)
      return pendingRequests[key].promise

    pendingRequests[key] = deferred = $q.defer()

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
        if config.noRetry404 or config.method isnt 'GET'
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
    ).finally ->
      $timeout((-> delete pendingRequests[key]), staleTolerance)


    deferred.promise

  rest.cleanCache = ->
    pendingRequests = {}

  rest.setStaleTolerance = (tolerance) ->
    staleTolerance = tolerance

  rest

