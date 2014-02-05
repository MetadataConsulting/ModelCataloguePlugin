angular.module('mc.coreServices', []).service("mcDataElementsService", ["$http", "$q",  ($http, $q) ->

  {
    list: () ->
      deferred = $q.defer()
      $http.get("/dataElement/").then (response) ->
        deferred.resolve(response.data)
      , (errorResponse) ->
        deferred.reject(errorResponse)

      deferred.promise

    get: (id) ->
      deferred = $q.defer()
      $http.get("/dataElement/#{id}").then (response) ->
        deferred.resolve(response.data)
      , (errorResponse) ->
        deferred.reject(errorResponse)

      deferred.promise


  }

])
