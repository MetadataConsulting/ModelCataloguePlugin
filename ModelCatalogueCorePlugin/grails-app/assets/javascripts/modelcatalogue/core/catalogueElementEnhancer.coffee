angular.module('mc.core.catalogueElementEnhancer', ['ui.router', 'mc.util.rest', 'mc.util.enhance', 'mc.util.names' ,'mc.core.modelCatalogueApiRoot']).config [ 'enhanceProvider', (enhanceProvider) ->
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

          @defaultExcludes = ['id','elementTypeName', 'elementType', 'elementTypes', 'incomingRelationships', 'outgoingRelationships', 'link', 'mappings']
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
            if self.elementType == "org.modelcatalogue.core.actions.Batch"
              $state.go('mc.actions.show', {id: self.id}); self
            else if self.elementTypeName == "Data Import"
              $state.go('mc.dataArchitect.imports.show', {id: self.id}); self
            else
              $state.go('mc.resource.show', {resource: names.getPropertyNameFromType(self.elementType), id: self.id}); self


          self.isInstanceOf   = (type) ->
            return false  if not type?
            return false  if not self.elementTypes
            return type in self.elementTypes

          self.getLabel = ->
              if @classifications? && @classifications.length > 0
                classificationNames = commaSeparatedList(@classifications)
                return "#{@name} (#{@elementTypeName} in #{classificationNames})"
              else if(@conceptualDomains? && @conceptualDomains.length>0)
                conceptualDomains = commaSeparatedList(@conceptualDomains)
                return "#{@name} (#{@elementTypeName} in #{conceptualDomains})"
              else if (@elementTypeName?)
                return "#{@name} (#{@elementTypeName})"
              else
                return @name

          self.getIcon = ->
            # TODO: move to bs package
            switch @elementType
              when 'org.modelcatalogue.core.Classification' then return "fa fa-fw fa-database"
              when 'org.modelcatalogue.core.Model' then return "fa fa-fw fa-cubes"
              when 'org.modelcatalogue.core.DataElement' then return "fa fa-fw fa-cube"
              when 'org.modelcatalogue.core.ConceptualDomain' then return "fa fa-fw fa-cogs"
              when 'org.modelcatalogue.core.ValueDomain' then return "fa fa-fw fa-cog"
              when 'org.modelcatalogue.core.EnumeratedType' then return "fa fa-fw fa-list-alt"
              when 'org.modelcatalogue.core.DataType' then return "fa fa-fw fa-list-alt"
              when 'org.modelcatalogue.core.MeasurementUnit' then return "fa fa-fw fa-tachometer"
              when 'org.modelcatalogue.core.Asset' then return "fa fa-fw fa-file-o"
              when 'org.modelcatalogue.core.RelationshipType' then return "fa fa-fw fa-link"
              when 'org.modelcatalogue.core.action.Action' then return "fa fa-fw fa-flash"
              else 'glyphicon glyphicon-file'


        getUpdatableProperties: () -> angular.copy(@updatableProperties)
      # wrap original element
      new CatalogueElement(element)
  ]
  enhanceProvider.registerEnhancerFactory('catalogueElement', condition, factory)
]