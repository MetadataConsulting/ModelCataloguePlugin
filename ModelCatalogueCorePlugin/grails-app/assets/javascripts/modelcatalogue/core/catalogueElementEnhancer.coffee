angular.module('mc.core.catalogueElementEnhancer', ['ui.router', 'mc.util.rest', 'mc.util.enhance', 'mc.util.names' ,'mc.core.modelCatalogueApiRoot', 'mc.core.catalogue', 'mc.core.elementEnhancer']).config [ 'enhanceProvider', (enhanceProvider) ->
  commaSeparatedList = (things)->
    names = []
    angular.forEach(things, (thing)->
      names.push thing.name
    )
    names.join(', ')

  condition = (element) -> element.hasOwnProperty('elementType') and element.hasOwnProperty('link')
  factory   = [ 'modelCatalogueApiRoot', 'rest', '$rootScope', '$state', 'names', 'enhance', (modelCatalogueApiRoot, rest, $rootScope, $state, names, enhance) ->
    (element) ->
      class CatalogueElement
        constructor: (element) ->
          angular.extend(@, element)

          @defaultExcludes = ['id','elementTypeName', 'classifiedName', 'elementType', 'incomingRelationships', 'outgoingRelationships', 'link', 'mappings']
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

          self.dataModels = [] unless self.dataModels
          self.classifications = self.dataModels

          self['delete']      = () ->
            enhance(rest(method: 'DELETE', url: "#{modelCatalogueApiRoot}#{self.link}")).then (result)->
              $rootScope.$broadcast 'catalogueElementDeleted', self
              result

          self.refresh        = () -> enhance rest method: 'GET', url: "#{modelCatalogueApiRoot}#{self.link}"
          self.validate       = () -> enhance rest method: 'POST', url: "#{modelCatalogueApiRoot}#{self.link}/validate", data: self.getUpdatePayload()
          self.update         = () ->
            enhance(rest(method: 'PUT', url: "#{modelCatalogueApiRoot}#{self.link}", data: self.getUpdatePayload())).then (result)->
              $rootScope.$broadcast 'catalogueElementUpdated', result
              result
          self.show           = () ->
            if self.isInstanceOf "batch"
              $state.go('mc.actions.show', {id: self.id}); self
            else if self.isInstanceOf "dataImport"
              $state.go('mc.dataArchitect.imports.show', {id: self.id}); self
            else if self.isInstanceOf "csvTransformation"
              $state.go('mc.csvTransformations.show', {id: self.id}); self
            else
              $state.go('mc.resource.show', {resource: names.getPropertyNameFromType(self.elementType), id: self.id}); self

          self.href           = () ->
            if self.isInstanceOf "batch"
              return $state.href('mc.actions.show', {id: self.id})
            if self.isInstanceOf "dataImport"
              return $state.href('mc.dataArchitect.imports.show', {id: self.id})
            if self.isInstanceOf "csvTransformation"
              return $state.href('mc.csvTransformations.show', {id: self.id})

            dataModelId = undefined
            if self.isInstanceOf "dataModel"
              dataModelId = self.id
            else if self.dataModels?.length > 0
              dataModelId = self.dataModels[0].id

            $state.href('mc.resource.show', {resource: names.getPropertyNameFromType(self.elementType), id: self.id, dataModelId: dataModelId})

          self.getLabel = ->
              return @classifiedName if @classifiedName?
              if @classifications? && @classifications.length > 0
                classificationNames = commaSeparatedList(@classifications)
                return "#{@name} (#{@getElementTypeName()} in #{classificationNames})"
              else if @isInstanceOf('relationship')
                return "#{@source.getLabel()} #{@type.sourceToDestination} {@destination.getLabel()}"
              else if @isInstanceOf('mapping')
                return "#{@source.getLabel()} #{@type.sourceToDestination} {@destination.getLabel()}"
              else if (@elementType?)
                return "#{@name} (#{@getElementTypeName()})"
              else
                return @name

          self.getUpdatableProperties = -> angular.copy(@updatableProperties)

      # wrap original element
      new CatalogueElement(element)
  ]
  enhanceProvider.registerEnhancerFactory('catalogueElement', condition, factory)
]