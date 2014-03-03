angular.module('mc.util.enhance', []).provider 'enhance', [ ->
  enhancers = []

  ###
  Registers new enhancer definiton, the enahcerFactory is either funciton or minification
  safe array (@see $injector).
  ###
  @registerEnhancerFactory = (name, condition, enhancerFactory, priority = 0) ->
    # we might want to add some assertion later
    enhancers.push({priority: priority, name: name, condition: condition, factory: enhancerFactory})

  # factory method
  @$get = [ '$injector', ($injector) ->
    # dependency aware initialization
    for enhancer in enhancers
      enhancer.enhancer = $injector.invoke(enhancer.factory)

    enhance = (result) ->
      # for object and array enhance deepth first
      if angular.isArray(result)
        for item, i in result when item?
          result[i] = enhance item
      else if angular.isObject(result)
        for name, value of result when value?
          result[name] = enhance value

      # enhance current object
      for enhancer in enhancers when enhancer.condition(result)
        result = enhancer.enhancer(result)

      # and return enhanced value
      result
  ]

  # Always return this from CoffeeScript AngularJS factory functions!
  @
]