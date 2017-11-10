angular.module('modelcatalogue.core.enhancersConf.dateEnhancer', ['mc.util.enhance']).config ['enhanceProvider', (enhanceProvider)->
  condition = (dateString) ->
    return false unless angular.isString(dateString)
    return false unless dateString.length == 20
    return /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2}(?:\.\d*)?)(?:Z|(\+|-)([\d|:]*))?$/.test dateString
  factory   = ->
    (dateString) -> new Date(dateString)

  enhanceProvider.registerEnhancerFactory('date', condition, factory)
]
