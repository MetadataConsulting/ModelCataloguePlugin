angular.module('mc.util.enhance', []).provider 'enhance', [ ->
  enhancers = []

  ###
    Registers new enhancer definiton, the enhancerFactory is either funciton or minification
    safe array (@see $injector).
  ###
  @registerEnhancerFactory = (name, condition, enhancerFactory, priority = 0) ->
    # we might want to add some assertion later
    enhancers.push({priority: priority, name: name, condition: condition, factory: enhancerFactory})

  # factory method
  @$get = [ '$injector', '$q', ($injector, $q) ->
    enhance = (result) ->
      return result unless result
      if result.then? and angular.isFunction(result.then)
        deferred = $q.defer()
        result.then(
          (response) ->
            deferred.resolve enhance(response)
        , (response) ->
          deferred.reject enhance(response)
        , (response) ->
          deferred.update enhance(response)
        )
        return deferred.promise

      # for object and array enhance deepth first
      if angular.isArray(result)
        for item, i in result when item?
          result[i] = enhance item
      else if angular.isObject(result)
        for name, value of result when value?
          result[name] = enhance value

      # enhance current object
      for enhancer in enhancers when enhancer.condition(result)
        enhancedBy = result.__enhancedBy ? []
        continue if enhancer.name in enhancedBy
        enhancedBy.push(enhancer.name)
        result = enhancer.enhancer(result)
        result.__enhancedBy = enhancedBy
        result.getEnhancedBy = -> result.__enhancedBy

      # and return enhanced value
      result

    # dependency aware initialization
    for enhancer in enhancers
      enhancer.enhancer = $injector.invoke(enhancer.factory, undefined, {enhance: enhance})
      enhancer.enhance  = enhance

    ###
      Returns list of names of all registered enhancers.
    ###
    enhance.getAvailableEnhancers = () ->
      enhancer.name for enhancer in enhancers

    ###
      Returns registered enhancer of given name or undefined.
    ###
    enhance.getEnhancer = (name) ->
      return enhancer.enhancer for enhancer in enhancers when enhancer.name == name

    ###
      Returns true if enhanced by enhancer of given name
    ###
    enhance.isEnhancedBy = (object, name) ->
      name in (object?.__enhancedBy ? [])

    ###
      Returns true if enhanced by any enhancer
    ###
    enhance.isEnhanced = (object) ->
      object?.__enhancedBy? and object.__enhancedBy.length != 0

    enhance
  ]

  # Always return this from CoffeeScript AngularJS factory functions!
  @
]