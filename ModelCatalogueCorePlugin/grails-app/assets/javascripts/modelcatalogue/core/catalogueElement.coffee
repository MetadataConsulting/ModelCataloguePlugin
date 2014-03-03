angular.module('mc.core.catalogueElement', ['mc.util.rest']).provider 'catalogueElement', [ 'restProvider', (restProvider) ->

  @$get = [ 'modelCatalogueApiRoot', (modelCatalogueApiRoot) ->
    (element, response, rest) ->

      class CatalogueElement
        @createEnhancer: (resource) ->
          (element) ->
            if element.hasOwnProperty('elementType')
              new CatalogueElement(element, resource)
            else
              element

        constructor: (element, @resource) ->
          @defaultExcludes = ['elementTypeName', 'elementType', 'incomingRelationships', 'outgoingRelationships']
          @updatableProperties = []

          for name, ignored of element
            unless name in @defaultExcludes
              @updatableProperties.push(name)

          angular.extend(@, element)
          if element.hasOwnProperty('incomingRelationships')
            incoming = element.incomingRelationships
            @incomingRelationships       = () -> rest({method: 'GET', url: "#{modelCatalogueApiRoot}#{incoming.link}"})
            @incomingRelationships.url   = incoming.link
            @incomingRelationships.total = incoming.count
          if element.hasOwnProperty('outgoingRelationships')
            outgoing = element.outgoingRelationships
            @outgoingRelationships = () -> rest({method: 'GET', url: "#{modelCatalogueApiRoot}#{outgoing.link}"})
            @outgoingRelationships.url   = outgoing.link
            @outgoingRelationships.total = outgoing.count

        delete:    () -> rest({method: 'DELETE', url: "#{modelCatalogueApiRoot}#{@link}"})
        validate:  () -> rest({method: 'get', url: "#{modelCatalogueApiRoot}#{@link}/validate"})

        update:    () ->
          payload = {}
          for name in @updatableProperties
            payload[name] = this[name]
          rest({method: 'PUT', url: "#{modelCatalogueApiRoot}#{@link}"}, payload)

        getUpdatableProperties: () -> angular.copy(@updatableProperties)
      # wrap original element
      new CatalogueElement(element)
  ]

  condition = (element) -> element.hasOwnProperty('elementType') and element.hasOwnProperty('link')

  restProvider.registerEnhancerFactory('catalogueElement', condition, @$get)

  @
]