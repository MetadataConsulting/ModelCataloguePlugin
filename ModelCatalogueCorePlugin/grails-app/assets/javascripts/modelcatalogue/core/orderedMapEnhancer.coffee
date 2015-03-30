angular.module('mc.core.orderedMapEnhancer', ['mc.util.enhance']).config ['enhanceProvider', (enhanceProvider)->
  condition = (orderedMap) -> orderedMap.hasOwnProperty('type') and orderedMap.type == 'orderedMap'
  factory   = [ ->
    orderedMapEnhancer = (orderedMap) ->

      map = {}

      for value in orderedMap.values
        map[value.key] = value.value

      orderedMap.get = (key) -> map[key]
      orderedMap


    orderedMapEnhancer.emptyOrderedMap = -> orderedMapEnhancer({
      type: 'orderedMap'
      values: []
    })

    orderedMapEnhancer
  ]

  enhanceProvider.registerEnhancerFactory('orderedMap', condition, factory)
]
