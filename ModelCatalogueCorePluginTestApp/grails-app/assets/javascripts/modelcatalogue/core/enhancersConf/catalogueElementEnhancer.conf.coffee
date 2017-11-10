angular.module('modelcatalogue.core.enhancersConf.catalogueElementEnhancer', ['ui.router', 'mc.util.rest', 'mc.util.enhance', 'mc.util.names' ,'mc.core.modelCatalogueApiRoot', 'mc.core.catalogue', 'modelcatalogue.core.enhancersConf.elementEnhancer', 'mc.core.serverPushUpdates', 'rx']).config [ 'enhanceProvider', (enhanceProvider) ->
  commaSeparatedList = (things)->
    names = []
    angular.forEach(things, (thing)->
      names.push thing.name
    )
    names.join(', ')

  updateFrom = (original, update, relaxed) ->
    if original == update
      return original

    if original and not original.minimal and original.lastUpdated and original.lastUpdated == update.lastUpdated
      return original

    if update and update.link and update.elementType
      unless relaxed
        for own originalKey of original
          # keep the private fields such as number of children in tree view
          if originalKey.indexOf('$') != 0
            delete original[originalKey]

      for newKey of update
        unless update[newKey] == null and update.minimal
          original[newKey] = update[newKey]
      original

  computeHref = (self, $state, names) ->
    if self.isInstanceOf "batch"
      return $state.href('simple.actions.show', {id: self.id})
    if self.isInstanceOf "csvTransformation"
      return $state.href('mc.csvTransformations.show', {id: self.id})
    if self.isInstanceOf "dataModelPolicy"
      return $state.href('simple.resource.show', {id: self.id, resource: 'dataModelPolicy'})
    if self.isInstanceOf "relationships"
      return $state.href('mc.resource.show.property', {dataModelId: self.element.getDataModelId(), id: self.element.id, resource: names.getPropertyNameFromType(self.element.elementType), property: self.property})
    if self.isInstanceOf "enumeratedValue"
      return $state.href('mc.resource.show.property', {resource: 'enumeratedType', id: self.id, dataModelId: self.getDataModelId(), property: 'enumerations'})
    if self.isInstanceOf "versions"
      return $state.href('mc.resource.show.property', {resource: 'dataModel', id:  self.getDataModelId(), dataModelId: self.getDataModelId(), property: 'history'})
    if self.getDataModelId() != 'catalogue'
      return $state.href('mc.resource.show', {resource: names.getPropertyNameFromType(self.elementType), id: self.id, dataModelId: self.getDataModelId()})
    $state.href('simple.resource.show', {resource: names.getPropertyNameFromType(self.elementType), id: self.id})


  condition = (element) -> angular.isObject(element) and element.hasOwnProperty('elementType') and element.hasOwnProperty('link')
  factory   =  (modelCatalogueApiRoot, rest, $rootScope, $state, names, enhance, serverPushUpdates, $cacheFactory) ->
    "ngInject"
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

          self['delete'] = () ->
            enhance(rest(method: 'DELETE', url: "#{modelCatalogueApiRoot}#{self.link}")).then (result)->
              if(!self.isInstanceOf('dataModel'))
                 $rootScope.$broadcast 'catalogueElementDeleted', self
              result

          self.execute = (tail, method = 'GET', data = undefined) ->
            params =
              method: method
              url:  "#{modelCatalogueApiRoot}#{self.link}/#{tail}"

            params.data = data if data

            enhance rest params

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


          self.show           = (reload = false) ->
            if self.isInstanceOf "batch"
              return $state.go('simple.actions.show', {id: self.id}, {reload: reload}); self
            if self.isInstanceOf "csvTransformation"
              return $state.go('mc.csvTransformations.show', {id: self.id}, {reload: reload}); self
            if self.isInstanceOf "dataModelPolicy"
              return $state.go('simple.resource.show', {id: self.id, resource: 'dataModelPolicy'}) ; self
            if self.isInstanceOf "relationships"
              return $state.go('mc.resource.show.property', {dataModelId: self.element.getDataModelId(), id: self.element.id, resource: names.getPropertyNameFromType(self.element.elementType), property: self.property}, {reload: reload})
            if self.isInstanceOf "enumeratedValue"
              return $state.go('mc.resource.show.property', {resource: 'enumeratedType', id: self.id, dataModelId: self.getDataModelId(), property: 'enumerations'}, {reload: reload}) ; self
            if self.isInstanceOf "versions"
              return $state.go('mc.resource.show.property', {resource: 'dataModel', id:  self.getDataModelId(), dataModelId: self.getDataModelId(), property: 'history'}, {reload: reload}) ; self
            if self.getDataModelId() != 'catalogue'
              return $state.go('mc.resource.show', {resource: names.getPropertyNameFromType(self.elementType), id: self.id, dataModelId: self.getDataModelId()}, {reload: reload})
            $state.go('simple.resource.show', {resource: names.getPropertyNameFromType(self.elementType), id: self.id}, {reload: reload}) ; self

          self.href = ->
            return self.$$href if self.$$href
            return self.$$href = computeHref(self, $state, names)

          self.getLabel = ->
              return @classifiedName if @classifiedName?
              if @classifications? && @classifications.length > 0
                classificationNames = commaSeparatedList(@classifications)
                return "#{@name} (#{@getElementTypeName()} in #{classificationNames})"
              if @isInstanceOf('relationship')
                return "#{@source.getLabel()} #{@type.sourceToDestination} #{@destination.getLabel()}"
              if @isInstanceOf('mapping')
                return "#{@source.getLabel()} #{@type.sourceToDestination} #{@destination.getLabel()}"
              if (@elementType?)
                return "#{@name} (#{@getElementTypeName()})"

              return @name

          self.updateFrom = (update) ->
              updateFrom this, update

          self.getUpdatableProperties = -> angular.copy(@updatableProperties)

          self.getDataModelWithVersionAndId = ->
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

            semver = "rev#{versionNumber}" unless semver

            return "#{@latestVersionId}@#{semver}" if @isInstanceOf('dataModel')

            return "#{ret} #{@latestVersionId}@#{semver}"


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
              return null

            semver = "rev#{versionNumber}" unless semver

            return semver if @isInstanceOf('dataModel')

            return "#{ret} #{semver}"

          self.getSemanticVersion = ->
            semver = ''
            versionNumber = 1
            if @dataModel
              semver = @dataModel.semanticVersion
              versionNumber = @versionNumber
            else if @isInstanceOf('dataModel')
              semver = @semanticVersion
              versionNumber = @versionNumber
            else
              semver = '0.0.0'

            semver = "rev#{versionNumber}" unless semver

            return semver

          self.getVersionAndId = ->
            return null unless @getExternalId()
            "#{@getExternalId()}@#{@getSemanticVersion()}"

          self.getExternalId = ->
            return @latestVersionId if @modelCatalogueId?.indexOf('http') == 0
            return @modelCatalogueId if @modelCatalogueId
            return @latestVersionId

          self.getDataModelStatus = ->
            if @dataModel
              return @dataModel.status
            if @isInstanceOf('dataModel')
              return @status

            return 'PENDING'

          self.setupUpdateHook = ->
            if @isInstanceOf('catalogueElement')
              serverPushUpdates.subscribe "/topic/changes#{@link}", (element) =>
                updateFrom(@, element, true)
                $rootScope.$broadcast 'catalogueElementUpdated', enhance(@)

          self.focus = ->
            self.execute('path').then (response) ->
              path = response

              return if not path
              return if path.length <= 1

              # need to add list node

              path.splice(1, 0, path[1].substring(0, path[1].lastIndexOf('/')) + '/all')

              $rootScope.$broadcast('expandTreeview', path)

      if element instanceof CatalogueElement
        # already enhanced
        return element

      cache = $cacheFactory.get('CatalogueElement')
      cache = $cacheFactory('CatalogueElement') if not cache

      cached = cache.get(element.link)

      if cached
        delete element.minimal
        updateFrom(cached, element, true)
        return cached

      # wrap original element
      enhanced = new CatalogueElement(element)
      cache.put(element.link, enhanced)

      enhanced.setupUpdateHook()
      enhanced

    catalogueElementEnhancer.updateFrom = updateFrom

    catalogueElementEnhancer

  enhanceProvider.registerEnhancerFactory('catalogueElement', condition, factory)
]
