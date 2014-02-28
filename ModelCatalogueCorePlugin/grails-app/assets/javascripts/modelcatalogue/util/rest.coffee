angular.module('mc.util.rest', []).provider 'rest', [ ->
  enhancers = []

  ###
  Registers new enhancer definiton, the enahcerFactory is either funciton or minification
  safe array (@see $injector).
  ###
  @registerEnhancerFactory = (name, condition, enhancerFactory, priority = 0) ->
    # we might want to add some assertion later
    enhancers.push({priority: priority, name: name, condition: condition, factory: enhancerFactory})

  # factory method
  @$get = [ '$q', '$http', '$injector', ($q, $http, $injector) ->
    # dependency aware initialization
    for enhancer in enhancers
      enhancer.enhancer = $injector.invoke(enhancer.factory)

    enhance = (result, response, rest) ->
      # for object and array enhance deepth first
      if angular.isArray(result)
        for item, i in result when item?
          result[i] = enhance item, response, rest
      else if angular.isObject(result)
        for name, value of result when value?
          result[name] = enhance value, response, rest

      # enhance current object
      for enhancer in enhancers when enhancer.condition(result)
        result = enhancer.enhancer(result, response, rest)

      # and return enhanced value
      result

    rest = (config) ->
      deferred = $q.defer()

      $http(config).then(
        (response) ->
          if !response.data and response.status >=200 and response.status < 300
            deferred.resolve enhance(response.status, response, rest)
          else if !response.data.errors?
            deferred.resolve enhance(response.data, response, rest)
          else
            deferred.reject enhance(response, response, rest)
      , (response) ->
        deferred.reject enhance(response, response, rest)
      )
      deferred.promise
  ]

  # Always return this from CoffeeScript AngularJS factory functions!
  @
]