angular.module('mc.core.catalogueElementEnhancer', ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot']).config [ 'enhanceProvider', (enhanceProvider) ->
  condition = (element) -> element.hasOwnProperty('elementType') and element.hasOwnProperty('link')
  factory   = [ 'modelCatalogueApiRoot', 'rest', '$rootScope', (modelCatalogueApiRoot, rest, $rootScope) ->
    (element, enhance = @enhance) ->
      class CatalogueElement
        constructor: (element) ->
          angular.extend(@, element)

          @defaultExcludes = ['id','elementTypeName', 'elementType', 'incomingRelationships', 'outgoingRelationships', 'link', 'mappings']
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

          self = @

          self['delete']  = () -> enhance rest method: 'DELETE', url: "#{modelCatalogueApiRoot}#{self.link}"
          self.validate       = () -> enhance rest method: 'POST', url: "#{modelCatalogueApiRoot}#{self.link}/validate", data: self.getUpdatePayload()
          self.update         = () -> enhance rest method: 'PUT', url: "#{modelCatalogueApiRoot}#{self.link}", data: self.getUpdatePayload()
          self.show           = () -> $rootScope.$broadcast('showCatalogueElement', self) ; self

        getUpdatableProperties: () -> angular.copy(@updatableProperties)
      # wrap original element
      new CatalogueElement(element)
  ]
  enhanceProvider.registerEnhancerFactory('catalogueElement', condition, factory)
]