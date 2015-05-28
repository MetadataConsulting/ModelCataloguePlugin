angular.module('mc.core.orderedMapEnhancer', ['mc.util.enhance']).config ['enhanceProvider', (enhanceProvider)->
  condition = (orderedMap) -> orderedMap.hasOwnProperty('type') and orderedMap.type == 'orderedMap'
  factory   = [ 'enhance', (enhance)->
    orderedMapEnhancer = (orderedMap) ->

      orderedMap.get = (key) -> @access(key)()
      orderedMap.access = (key) ->
        getterSetter = (newValue) ->
          if arguments.length
            for value in orderedMap.values
              if value.key == key
                value.value = newValue
                return newValue
            orderedMap.values.push(key: key, value: newValue)
            return newValue
          for value in orderedMap.values
            if value.key == key
              return value.value
          return undefined

        getterSetter.get = -> getterSetter()
        getterSetter.set = (newValue) -> getterSetter(newValue)
        getterSetter.remove = ->
          result = undefined
          for value, i in orderedMap.values.reverse()
            if value.key == key
              result = value.value
              orderedMap.values.splice(i, 1)
          result

        getterSetter


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
