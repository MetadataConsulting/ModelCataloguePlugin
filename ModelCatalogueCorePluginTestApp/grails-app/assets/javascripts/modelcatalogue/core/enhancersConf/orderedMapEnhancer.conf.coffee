angular.module('modelcatalogue.core.enhancersConf.orderedMapEnhancer', ['mc.util.enhance']).config ['enhanceProvider', (enhanceProvider)->
  condition = (orderedMap) -> orderedMap.hasOwnProperty('type') and orderedMap.type == 'orderedMap'
  factory   = [ 'enhance', (enhance)->
    orderedMapEnhancer = (orderedMap) ->
      ensureValuesAreArray = (map) ->
        return if map.values and angular.isArray(map.values)
        map.values = []

      orderedMap.get = (key) -> @access(key).get()
      orderedMap.access = (key, allowEmpty = false) ->
        self = @
        set = (newValue) ->
          ensureValuesAreArray(self)
          for value in self.values
            if value.key == key
              value.value = newValue
              return newValue
          # only push if the value is defined
          self.values.push(key: key, value: newValue) if allowEmpty or angular.isDefined(newValue) or not key
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
            set(if newValue? then  parseInt(newValue, 10) else undefined)
          else
            parseInt(get(), 10)

        getterSetter.asBoolean = (newValue) ->
          if arguments.length
            set(if newValue? then newValue ? 'true' : 'false' else undefined)
          else
            if get()? then get() == 'true' else undefined

        getterSetter


      orderedMap.clearIfOnlyContainsPlaceholder = ->
        ensureValuesAreArray(@)
        if @values.length == 1 and not @values[0].value == null and @values[0].hasOwnProperty('key')
          @values = []

      orderedMap.addPlaceholderIfEmpty = ->
        ensureValuesAreArray(@)
        if @values.length == 0
          @values = [{key: ''}]
      orderedMap

      orderedMap.remove = (key) ->
        if angular.isNumber(key)
          @values.splice(key, 1)
          @addPlaceholderIfEmpty()
          return

        indexOf = -1
        for value, index in @values
          if value.key == key
            indexOf = index
            break

        @remove(indexOf) if indexOf >= 0


      orderedMap.asMap = ->
        ret = {}

        for value in @values
          ret[value.key] = value.value

        return ret

      orderedMap.updateFrom = (map, allowEmpty = false) ->
        return orderedMap if not map

        if condition(map)
          for value in map.values
            @access(value.key, allowEmpty).set(value.value)
          return orderedMap

        for key, value of map
          @access(key, allowEmpty).set(value)

        return orderedMap

      orderedMap



    orderedMapEnhancer.emptyOrderedMap = (noPlaceholder = false) ->

      if noPlaceholder
        return enhance orderedMapEnhancer({
          type: 'orderedMap'
          values: []
        })

      return enhance orderedMapEnhancer({
        type: 'orderedMap'
        values: [{key: ''}]
      })

    orderedMapEnhancer
  ]

  enhanceProvider.registerEnhancerFactory('orderedMap', condition, factory)
]
