angular.module('mc.core.catalogueElement', ['mc.util.rest', 'mc.util.enhance']).config [ 'enhanceProvider', (enhanceProvider) ->
  condition = (element) -> element.hasOwnProperty('elementType') and element.hasOwnProperty('link')
  factory   = [ 'modelCatalogueApiRoot', 'rest', (modelCatalogueApiRoot, rest) ->
    (element, enhance = @enhance) ->
      class CatalogueElement
        constructor: (element) ->
          @defaultExcludes = ['id','elementTypeName', 'elementType', 'incomingRelationships', 'outgoingRelationships', 'link']
          @getUpdatePayload = () ->
            payload = {}
            for name in @updatableProperties
              value = this[name]
              continue if angular.isFunction(value)
              if angular.isObject(value)
                if value.hasOwnProperty('id')
                  value = {id: value.id}
                else
                  continue
              payload[name] = value
            payload

          @updatableProperties = []

          for name, ignored of element
            unless name in @defaultExcludes
              @updatableProperties.push(name)

          angular.extend(@, element)

        delete:    () -> enhance rest method: 'DELETE', url: "#{modelCatalogueApiRoot}#{@link}"
        validate:  () -> enhance rest method: 'POST', url: "#{modelCatalogueApiRoot}#{@link}/validate", data: @getUpdatePayload()
        update:    () -> enhance rest method: 'PUT', url: "#{modelCatalogueApiRoot}#{@link}", data: @getUpdatePayload()

        getUpdatableProperties: () -> angular.copy(@updatableProperties)
      # wrap original element
      new CatalogueElement(element)
  ]
  enhanceProvider.registerEnhancerFactory('catalogueElement', condition, factory)
]