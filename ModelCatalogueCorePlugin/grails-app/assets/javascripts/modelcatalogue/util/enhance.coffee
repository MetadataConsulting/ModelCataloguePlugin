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

      deferred = $q.defer()

      result.__enhance__promise__ = deferred.promise

      # for object and array enhance deepth first
      if angular.isArray(result)
        angular.forEach result, (item, i) ->
          return if not item?
          if item.__enhance__promise__
            item.__enhance__promise__.then (enhanced) ->
              result[i] = enhanced
          else
            result[i] = enhance item
      else if angular.isObject(result)
        angular.forEach result, (value, name) ->
          return if not value?
          return if name is '__enhance__promise__'
          if value.__enhance__promise__
            value.__enhance__promise__.then (enhanced) ->
              result[name] = enhanced
          else
            result[name] = enhance value

      # enhance current object
      for enhancer in enhancers when enhancer.condition(result)
        enhancedBy = result.__enhancedBy
        enhancedBy = [] unless enhancedBy and angular.isArray(enhancedBy)

        continue if enhancer.name in enhancedBy
        enhancedBy.push(enhancer.name)
        result = enhancer.enhancer(result)
        result.__enhancedBy = enhancedBy
        result.getEnhancedBy = -> result.__enhancedBy

      deferred.resolve result
      delete result.__enhance__promise__

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
