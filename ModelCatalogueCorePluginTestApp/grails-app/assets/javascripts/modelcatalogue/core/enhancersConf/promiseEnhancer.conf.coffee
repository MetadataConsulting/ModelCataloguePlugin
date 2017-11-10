angular.module('modelcatalogue.core.enhancersConf.promiseEnhancer', ['mc.util.rest', 'mc.util.enhance']).config ['enhanceProvider', (enhanceProvider)->
  condition = (result) -> result.then? and angular.isFunction(result.then)
  factory   = ['$q', 'enhance', ($q, enhance) ->
    promiseEnhancer = (result) ->
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
    promiseEnhancer
  ]

  enhanceProvider.registerEnhancerFactory('promise', condition, factory)
]
