angular.module('mc.core.elementEnhancer', ['mc.util.rest', 'mc.util.enhance']).config ['enhanceProvider', (enhanceProvider)->
  condition = (object) -> object.hasOwnProperty('elementType')
  factory   = ['catalogue', 'names', (catalogue, names) ->
    isInstanceOfEnhancer = (object) ->
      object.isInstanceOf = (type) ->
        catalogue.isInstanceOf object.elementType, type
      object

      object.getIcon = ->
        catalogue.getIcon(@elementType)

      object.getElementTypeName = ->
        return @elementTypeName if @elementTypeName
        @elementTypeName = names.getNaturalName(object.getResourceName())

      object.getResourceName = ->
        names.getPropertyNameFromType(@elementType)

      object
    isInstanceOfEnhancer
  ]

  enhanceProvider.registerEnhancerFactory('element', condition, factory)
]
