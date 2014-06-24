angular.module('mc.core.catalogueElementEnhancer', ['ui.router', 'mc.util.rest', 'mc.util.enhance', 'mc.util.names' ,'mc.core.modelCatalogueApiRoot']).config [ 'enhanceProvider', (enhanceProvider) ->
  condition = (element) -> element.hasOwnProperty('elementType') and element.hasOwnProperty('link')
  factory   = [ 'modelCatalogueApiRoot', 'rest', '$rootScope', '$state', 'names', 'enhance', (modelCatalogueApiRoot, rest, $rootScope, $state, names, enhance) ->
    (element) ->
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

          self['delete']      = () ->
            enhance(rest(method: 'DELETE', url: "#{modelCatalogueApiRoot}#{self.link}")).then (result)->
              $rootScope.$broadcast 'catalogueElementDeleted', self
              result

          self.refresh        = () -> enhance rest method: 'GET', url: "#{modelCatalogueApiRoot}#{self.link}"
          self.validate       = () -> enhance rest method: 'POST', url: "#{modelCatalogueApiRoot}#{self.link}/validate", data: self.getUpdatePayload()
          self.update         = () -> enhance rest method: 'PUT', url: "#{modelCatalogueApiRoot}#{self.link}", data: self.getUpdatePayload()
          self.show           = () ->
            if(self.elementTypeName=="Data Import")
              $state.go('mc.dataArchitect.imports.show', {id: self.id}); self
            else
              $state.go('mc.resource.show', {resource: names.getPropertyNameFromType(self.elementType), id: self.id}); self


          self.isInstanceOf   = (type) ->
            # TODO create hierarchy service
            return false  if not type?
            return false  if type.indexOf('org.modelcatalogue.core.') == -1
            return false  if self.elementType is 'org.modelcatalogue.core.RelationshipType' and type isnt 'org.modelcatalogue.core.RelationshipType'
            return false  if self.elementType is 'org.modelcatalogue.core.Relationship'     and type isnt 'org.modelcatalogue.core.Relationship'
            return false  if self.elementType is 'org.modelcatalogue.core.Mapping'          and type isnt 'org.modelcatalogue.core.Mapping'
            return true   if type is 'org.modelcatalogue.core.CatalogueElement'
            return true   if type in ['org.modelcatalogue.core.ExtendibleElement', 'org.modelcatalogue.core.PublishedElement'] and self.elementType in ['org.modelcatalogue.core.Asset', 'org.modelcatalogue.core.Model', 'org.modelcatalogue.core.DataElement']
            return self.elementType == type



        getUpdatableProperties: () -> angular.copy(@updatableProperties)
      # wrap original element
      new CatalogueElement(element)
  ]
  enhanceProvider.registerEnhancerFactory('catalogueElement', condition, factory)
]