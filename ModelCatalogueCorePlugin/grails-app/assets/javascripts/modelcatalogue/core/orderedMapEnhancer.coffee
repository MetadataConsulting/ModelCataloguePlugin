angular.module('mc.core.orderedMapEnhancer', ['mc.util.enhance']).config ['enhanceProvider', (enhanceProvider)->
  condition = (orderedMap) -> orderedMap.hasOwnProperty('type') and orderedMap.type == 'orderedMap'
  factory   = [ 'enhance', (enhance)->
    orderedMapEnhancer = (orderedMap) ->
      ensureValuesAreArray = (map) ->
        return if map.values and angular.isArray(map.values)
        map.values = []

      orderedMap.get = (key) -> @access(key).get()
      orderedMap.access = (key) ->
        self = @
        set = (newValue) ->
          ensureValuesAreArray(self)
          for value in self.values
            if value.key == key
              value.value = newValue
              return newValue
          # only push if the value is defined
          self.values.push(key: key, value: newValue) if newValue or not key
          return newValue
        get =  ->
          ensureValuesAreArray(self)
          for value in self.values
            if value.key == key
              return value.value
          return undefined

        getterSetter = (newValue) ->
          if arguments.length
            set(newValue)
          else
            get()

        getterSetter.get = -> get()
        getterSetter.set = (newValue) -> set(newValue)
        getterSetter.remove = ->
          ensureValuesAreArray(self)
          result = undefined
          for value, i in self.values[..].reverse()
            if value.key == key
              result = value.value
              self.values.splice(i, 1)
          result

        getterSetter.asInt = (newValue) ->
          if arguments.length
            set(if newValue then  parseInt(newValue, 10) else undefined)
          else
            parseInt(get(), 10)

        getterSetter


      orderedMap.clearIfOnlyContainsPlaceholder = ->
        ensureValuesAreArray(@)
        if @values.length == 1 and not @values[0].value
          @values = []

      orderedMap.addPlaceholderIfEmpty = ->
        ensureValuesAreArray(@)
        if @values.length == 0
          @values = [{key: ''}]
      orderedMap


    orderedMapEnhancer.emptyOrderedMap = -> enhance orderedMapEnhancer({
      type: 'orderedMap'
      values: [{key: ''}]
    })

    orderedMapEnhancer
  ]

  enhanceProvider.registerEnhancerFactory('orderedMap', condition, factory)
]
