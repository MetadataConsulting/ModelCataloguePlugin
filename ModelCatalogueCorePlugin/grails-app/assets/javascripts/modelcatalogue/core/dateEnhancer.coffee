angular.module('mc.core.dateEnhancer', ['mc.util.enhance']).config ['enhanceProvider', (enhanceProvider)->
  condition = (dateString) -> /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2}(?:\.\d*)?)(?:Z|(\+|-)([\d|:]*))?$/.test dateString
  factory   = ->
    (dateString) -> new Date(dateString)

  enhanceProvider.registerEnhancerFactory('date', condition, factory)
]
