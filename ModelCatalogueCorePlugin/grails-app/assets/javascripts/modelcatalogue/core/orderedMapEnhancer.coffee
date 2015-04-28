angular.module('mc.core.orderedMapEnhancer', ['mc.util.enhance']).config ['enhanceProvider', (enhanceProvider)->
  condition = (orderedMap) -> orderedMap.hasOwnProperty('type') and orderedMap.type == 'orderedMap'
  factory   = [ 'enhance', (enhance)->
    orderedMapEnhancer = (orderedMap) ->

      map = {}

      for value in orderedMap.values
        map[value.key] = value.value

      orderedMap.get = (key) -> map[key]

      orderedMap.clearIfOnlyContainsPlaceholder = ->
        if orderedMap.values.length == 1 and not orderedMap.values[0].value
          orderedMap.values = []

      orderedMap.addPlaceholderIfEmpty = ->
        if orderedMap.values.length == 0
          orderedMap.values = [{key: ''}]
      orderedMap


    orderedMapEnhancer.emptyOrderedMap = -> enhance orderedMapEnhancer({
      type: 'orderedMap'
      values: [{key: ''}]
    })

    orderedMapEnhancer
  ]

  enhanceProvider.registerEnhancerFactory('orderedMap', condition, factory)
]
