angular.module('mc.core.catalogueElement', ['mc.util.rest', 'mc.util.enhance']).config [ 'enhanceProvider', (enhanceProvider) ->
  condition = (element) -> element.hasOwnProperty('elementType') and element.hasOwnProperty('link')
  factory   = [ 'modelCatalogueApiRoot', 'rest', (modelCatalogueApiRoot, rest) ->
    (element, enhance = @enhance) ->
      class CatalogueElement
        constructor: (element) ->
          @defaultExcludes = ['id','elementTypeName', 'elementType', 'incomingRelationships', 'outgoingRelationships', 'link']
          @listProperties  = ['outgoingRelationships', 'incomingRelationships', 'mappings' ]
          @getUpdatePayload = () ->
            payload = {}
            for name in @updatableProperties
              payload[name] = this[name]
            payload

          @updatableProperties = []

          for name, ignored of element
            unless name in @defaultExcludes
              @updatableProperties.push(name)

          angular.extend(@, element)
          for prop in @listProperties when element.hasOwnProperty(prop)
            list = element[prop]
            this[prop]       = () -> enhance rest method: 'GET', url: "#{modelCatalogueApiRoot}#{list.link}"
            this[prop].url   = list.link
            this[prop].total = list.count

        delete:    () -> enhance rest method: 'DELETE', url: "#{modelCatalogueApiRoot}#{@link}"
        validate:  () -> enhance rest method: 'POST', url: "#{modelCatalogueApiRoot}#{@link}/validate", data: @getUpdatePayload()
        update:    () -> enhance rest method: 'PUT', url: "#{modelCatalogueApiRoot}#{@link}", data: @getUpdatePayload()

        getUpdatableProperties: () -> angular.copy(@updatableProperties)
      # wrap original element
      new CatalogueElement(element)
  ]
  enhanceProvider.registerEnhancerFactory('catalogueElement', condition, factory)
]