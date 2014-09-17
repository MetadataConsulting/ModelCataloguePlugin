angular.module('mc.core.catalogueElementEnhancer', ['ui.router', 'mc.util.rest', 'mc.util.enhance', 'mc.util.names' ,'mc.core.modelCatalogueApiRoot', 'mc.core.catalogue']).config [ 'enhanceProvider', (enhanceProvider) ->
  commaSeparatedList = (things)->
    names = []
    angular.forEach(things, (thing)->
      names.push thing.name
    )
    names.join(', ')

  condition = (element) -> element.hasOwnProperty('elementType') and element.hasOwnProperty('link')
  factory   = [ 'modelCatalogueApiRoot', 'rest', '$rootScope', '$state', 'names', 'enhance', 'catalogue', (modelCatalogueApiRoot, rest, $rootScope, $state, names, enhance, catalogue) ->
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
            if self.isInstanceOf "batch"
              $state.go('mc.actions.show', {id: self.id}); self
            else if self.isInstanceOf "dataImport"
              $state.go('mc.dataArchitect.imports.show', {id: self.id}); self
            else if self.isInstanceOf "csvTransformation"
              $state.go('mc.csvTransformations.show', {id: self.id}); self
            else
              $state.go('mc.resource.show', {resource: names.getPropertyNameFromType(self.elementType), id: self.id}); self


          self.isInstanceOf   = (type) ->
            catalogue.isInstanceOf @elementType, type

          self.getLabel = ->
              if @classifications? && @classifications.length > 0
                classificationNames = commaSeparatedList(@classifications)
                return "#{@name} (#{@getElementTypeName()} in #{classificationNames})"
              else if(@conceptualDomains? && @conceptualDomains.length>0)
                conceptualDomains = commaSeparatedList(@conceptualDomains)
                return "#{@name} (#{@getElementTypeName()} in #{conceptualDomains})"
              else if (@elementType?)
                return "#{@name} (#{@getElementTypeName()})"
              else
                return @name

          self.getIcon = ->
            catalogue.getIcon(@elementType)

          self.getElementTypeName = ->
            return @elementTypeName if @elementTypeName
            @elementTypeName = names.getNaturalName(names.getPropertyNameFromType(@elementType))


        getUpdatableProperties: () -> angular.copy(@updatableProperties)
      # wrap original element
      new CatalogueElement(element)
  ]
  enhanceProvider.registerEnhancerFactory('catalogueElement', condition, factory)
]