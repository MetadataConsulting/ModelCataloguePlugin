angular.module('mc.util.rest', ['mc.util.messages', 'mc.util.objectVisitor']).factory 'rest', ($q, $log, $http, $rootScope, $timeout, objectVisitor) ->

  staleTolerance = 100 # do not request again within this period (ms)
  pendingRequests = {}

  createKey = (config) ->
    method = config.method ? 'GET'
    url = config.url
    params = config.params ? {}

    return "#{method}:#{url}?#{"#{k}=#{v}" for k,v of params}"

  rest = (config) ->
    key = createKey(config)
    if config.join and (not config.method or config.method is 'GET') and pendingRequests.hasOwnProperty(key)
      return pendingRequests[key]

    deferred = $q.defer()

    if config.data

      config.data = objectVisitor.visit config.data, (value, name) ->
        unless value? or name in ['__enhancedBy', 'defaultExcludes', 'updatableProperties', 'original', 'availableReports']
          return undefined
        if value and name in ['ext']
          return value
        if angular.isDate(value) or angular.isFunction(value)
          return value
        if value and name in ['dataModel', 'classification', 'dataClass', 'measurementUnit', 'dataElement', 'dataType'] and angular.isObject(value) and value.hasOwnProperty('id')
          return {id: value.id, elementType: value.elementType}
        return value

# for debugging circular references place a breakpoint into the angular.toJson()
#      seen = [];
#      seenPlaces = []
#
#      objectVisitor.visit config.data, (value, name) ->
#        if value and angular.isObject(value)
#          return value if angular.isArray(value) and value.length == 0
#          if value and name in ['__enhancedBy', 'defaultExcludes', 'updatableProperties', 'original', 'availableReports']
#            return value
#          if angular.isDate(value) or angular.isFunction(value)
#            return value
#          idx = seen.indexOf(value)
#          if idx >= 0
#            $log.warn(value, " (", name, ") has been already seen as ", seenPlaces[0])
#          seen.push(value)
#          seenPlaces.push(name)
#        return value

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


    pendingRequests[key] = deferred.promise

  rest.cleanCache = ->
    pendingRequests = {}

  rest.setStaleTolerance = (tolerance) ->
    staleTolerance = tolerance

  rest

