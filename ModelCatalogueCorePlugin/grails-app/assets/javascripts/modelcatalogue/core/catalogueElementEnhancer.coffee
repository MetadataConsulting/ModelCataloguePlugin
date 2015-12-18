angular.module('mc.core.catalogueElementEnhancer', ['ui.router', 'mc.util.rest', 'mc.util.enhance', 'mc.util.names' ,'mc.core.modelCatalogueApiRoot', 'mc.core.catalogue', 'mc.core.elementEnhancer']).config [ 'enhanceProvider', (enhanceProvider) ->
  commaSeparatedList = (things)->
    names = []
    angular.forEach(things, (thing)->
      names.push thing.name
    )
    names.join(', ')

  updateFrom = (original, update) ->
    for originalKey of original
      if originalKey.indexOf('$') != 0 # keep the private fields such as number of children in tree view
        delete original[originalKey]

    for newKey of update
      original[newKey] = update[newKey]
    original




  condition = (element) -> element.hasOwnProperty('elementType') and element.hasOwnProperty('link')
  factory   = [ 'modelCatalogueApiRoot', 'rest', '$rootScope', '$state', 'names', 'enhance', (modelCatalogueApiRoot, rest, $rootScope, $state, names, enhance) ->
    catalogueElementEnhancer = (element) ->
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

          self.getDataModelId = ->
            return self.id if self.isInstanceOf "dataModel"
            return self.dataModels[0].id if self.dataModels?.length > 0 and self.dataModels[0]
            return 'catalogue'


          self.show           = () ->
            if self.isInstanceOf "batch"
              return $state.go('simple.actions.show', {id: self.id}); self
            if self.isInstanceOf "csvTransformation"
              return $state.go('mc.csvTransformations.show', {id: self.id}); self
            if self.isInstanceOf "relationships"
              return $state.go('mc.resource.show.property', {dataModelId: self.element.getDataModelId(), id: self.element.id, resource: names.getPropertyNameFromType(self.element.elementType), property: self.property})
            if self.isInstanceOf "enumeratedValue"
              $state.go('mc.resource.show.property', {resource: 'enumeratedType', id: self.id, dataModelId: self.getDataModelId(), property: 'enumerations'}) ; self
            if self.isInstanceOf "versions"
              $state.go('mc.resource.show.property', {resource: 'dataModel', id:  self.getDataModelId(), dataModelId: self.getDataModelId(), property: 'history'}) ; self
            if self.getDataModelId() != 'catalogue'
              return $state.go('mc.resource.show', {resource: names.getPropertyNameFromType(self.elementType), id: self.id, dataModelId: self.getDataModelId()})
            $state.go('simple.resource.show', {resource: names.getPropertyNameFromType(self.elementType), id: self.id}) ; self

          self.href = () ->
            if self.isInstanceOf "batch"
              return $state.href('simple.actions.show', {id: self.id})
            if self.isInstanceOf "csvTransformation"
              return $state.href('mc.csvTransformations.show', {id: self.id})
            if self.isInstanceOf "relationships"
              return $state.href('mc.resource.show.property', {dataModelId: self.element.getDataModelId(), id: self.element.id, resource: names.getPropertyNameFromType(self.element.elementType), property: self.property})
            if self.isInstanceOf "enumeratedValue"
              return $state.href('mc.resource.show.property', {resource: 'enumeratedType', id: self.id, dataModelId: self.getDataModelId(), property: 'enumerations'})
            if self.isInstanceOf "versions"
              return $state.href('mc.resource.show.property', {resource: 'dataModel', id:  self.getDataModelId(), dataModelId: self.getDataModelId(), property: 'history'})
            if self.getDataModelId() != 'catalogue'
              return $state.href('mc.resource.show', {resource: names.getPropertyNameFromType(self.elementType), id: self.id, dataModelId: self.getDataModelId()})
            $state.href('simple.resource.show', {resource: names.getPropertyNameFromType(self.elementType), id: self.id})

          self.getLabel = ->
              return @classifiedName if @classifiedName?
              if @classifications? && @classifications.length > 0
                classificationNames = commaSeparatedList(@classifications)
                return "#{@name} (#{@getElementTypeName()} in #{classificationNames})"
              if @isInstanceOf('relationship')
                return "#{@source.getLabel()} #{@type.sourceToDestination} {@destination.getLabel()}"
              if @isInstanceOf('mapping')
                return "#{@source.getLabel()} #{@type.sourceToDestination} {@destination.getLabel()}"
              if (@elementType?)
                return "#{@name} (#{@getElementTypeName()})"

              return @name

          self.updateFrom = (update) ->
              updateFrom this, update

          self.getUpdatableProperties = -> angular.copy(@updatableProperties)

          self.getDataModelWithVersion = ->
            ret = ''
            semver = ''
            versionNumber = 1
            if @dataModel
              ret = @dataModel.name
              semver = @dataModel.semanticVersion
              versionNumber = @versionNumber
            else if @isInstanceOf('dataModel')
              ret = @name
              semver = @semanticVersion
              versionNumber = @versionNumber
            else
              ret = 'None'
              semver = '0.0.0'

            semver = "0.0.#{versionNumber}" unless semver

            return "#{ret} #{semver}"

          self.getDataModelStatus = ->
            if @dataModel
              return @dataModel.status
            if @isInstanceOf('dataModel')
              return @status

            return 'PENDING'

      # wrap original element
      new CatalogueElement(element)

    catalogueElementEnhancer.updateFrom = updateFrom

    catalogueElementEnhancer
  ]
  enhanceProvider.registerEnhancerFactory('catalogueElement', condition, factory)
]