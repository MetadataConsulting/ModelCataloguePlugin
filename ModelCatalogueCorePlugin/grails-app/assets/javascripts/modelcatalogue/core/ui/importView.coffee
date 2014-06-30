angular.module('mc.core.ui.importView', ['mc.core.catalogueElementEnhancer', 'mc.core.listReferenceEnhancer', 'mc.core.listEnhancer', 'mc.util.names', 'mc.util.messages', 'mc.core.ui.columns', 'ui.router']).directive 'importView',  [-> {
    restrict: 'E'
    replace: true
    scope:
      element: '='
      property: '=?'
      id: '@'

    templateUrl: 'modelcatalogue/core/ui/importView.html'

    controller: ['$scope', '$log', '$filter', '$q', '$state', 'enhance', 'names', 'columns', 'messages', '$rootScope', 'catalogueElementResource','modelCatalogueDataArchitect', 'security', ($scope, $log, $filter, $q, $state, enhance, names, columns, messages, $rootScope, catalogueElementResource, modelCatalogueDataArchitect, security) ->
      propExcludes     = ['version', 'name', 'description', 'incomingRelationships', 'outgoingRelationships', 'availableReports', 'downloadUrl']
      listEnhancer    = enhance.getEnhancer('list')
      getPropertyVal  = (propertyName) ->
        (element) -> element[propertyName]

      getObjectSize   = (object) ->
        size = 0
        angular.forEach object, () ->
          size++
        size

      $scope.property ?= $rootScope?.$stateParams?.property
      $scope.reports  = []

      onElementUpdate = (element) ->
        resource = catalogueElementResource(element.elementType) if element and element.elementType

        activeTabSet     = false

        tabs = []

        for name, fn of element when enhance.isEnhancedBy(fn, 'listReference')
          if name in propExcludes
            continue
          tabDefinition =
            heading:  names.getNaturalName(name)
            value:    listEnhancer.createEmptyList(fn.itemType)
            disabled: fn.total == 0
            loader:   fn
            type:     'decorated-list'
            columns:   columns(fn.itemType)
            actions:  []
            name:     name
            reports:  []


          if fn.itemType == 'org.modelcatalogue.core.dataarchitect.ImportRow'&& tabDefinition.name != 'imported'
            tabDefinition.actions.push {
              title:  'resolve action'
              icon:   'remove'
              type:   'default'
              action: (rel) ->
                deferred = $q.defer()
                messages.confirm('Resolve Actions', "Do you really want to resolve all actions : '#{rel.actions}'?").then () ->
                  rel.action().then ->
                    messages.success('Row actions resolved!', "actions are resolved")
                    # reloads the table
                    deferred.resolve(true)
                  , (response) ->
                    if response.status == 404
                      messages.error('Error resolving actions', 'Actions cannot be resolve, it probably does not exist anymore. The table was refreshed to get the most up to date results.')
                      deferred.resolve(true)
                    else
                      messages.error('Error on action', 'Actions cannot be resolved. Possibly there is an error that needs user input')

                deferred.promise

            }

          if tabDefinition.name == $scope.property
            tabDefinition.active = true
            activeTabSet = true

          tabs.push tabDefinition


        for name, obj of element
          if name in propExcludes
            continue
          unless angular.isObject(obj) and !angular.isArray(obj) and !enhance.isEnhanced(obj)
            continue
          tabDefinition =
            name:       name
            heading:    names.getNaturalName(name)
            value:      obj ? {}
            original:   angular.copy(obj ? {})
            properties: []
            type:       if security.hasRole('CURATOR') then 'simple-object-editor' else 'properties-pane'
            isDirty:    () -> angular.equals(@original, @value)
            reset:      () -> @value = angular.copy @original
            update:     () ->
              if not resource
                messages.error("Cannot update property #{names.getNaturalName(self.name)} of #{element.name}. See application logs for details.")
                return

              payload = {
                id: element.id
              }
              payload[@name] = angular.copy(@value)
              self = @
              resource.update(payload).then (updated) ->
                $scope.element = updated
                messages.success("Property #{names.getNaturalName(self.name)} of #{element.name} successfully updated")
                updated
              ,  ->
                messages.error("Cannot update property #{names.getNaturalName(self.name)} of #{element.name}. See application logs for details.")


          for key, value of obj when not angular.isObject(value)
            tabDefinition.properties.push {
              label: key
              value: getPropertyVal(key)
            }

          if tabDefinition.name == $scope.property
            tabDefinition.active = true
            activeTabSet = true

          tabs.push tabDefinition


        tabs = $filter('orderBy')(tabs, 'heading')

        if enhance.isEnhancedBy(element, 'catalogueElement')
          newProperties = []
          for prop in element.getUpdatableProperties()
            obj = element[prop]
            if prop in propExcludes
              continue
            if enhance.isEnhancedBy(obj, 'listReference')
              continue
            if (angular.isObject(obj) and !angular.isArray(obj) and !enhance.isEnhanced(obj))
              continue
            newProperties.push(label: names.getNaturalName(prop), value: getPropertyVal(prop))

          tabDefinition =
            heading:    'Properties'
            name:       'properties'
            value:      element
            disabled:   getObjectSize(newProperties) == 0
            properties: newProperties
            type:       'properties-pane'

          if tabDefinition.name == $scope.property
            tabDefinition.active = true
            activeTabSet = true

          tabs.unshift tabDefinition


        showTabs = false
        if not activeTabSet
          for tab in tabs
            if not tab.disabled
              tab.active = true
              $scope.property = tab.name
              showTabs = true
              break
        else
          showTabs = true

        $scope.tabs = tabs
        $scope.showTabs = showTabs

      $scope.tabs   = []
      $scope.select = (tab) ->
        $scope.property = tab.name
        return if not tab.loader?
        if !tab.disabled and tab.value.empty
          tab.loader().then (result) ->
            tab.value       = result


      # watches
      $scope.$watch 'element', onElementUpdate

      $scope.resolveAll = () ->
        modelCatalogueDataArchitect.resolveAll($scope.element.id).then (result)->
          $rootScope.$broadcast 'actionsResolved', $scope.element

      $scope.ingestQueue = () ->
        modelCatalogueDataArchitect.ingestQueue($scope.element.id).then (result)->
          $rootScope.$broadcast 'actionsResolved', $scope.element

      refreshElement = () ->
        if $scope.element
          $scope.element.refresh().then (refreshed)->
            $scope.element = refreshed

      $scope.$on 'catalogueElementCreated', refreshElement
      $scope.$on 'catalogueElementDeleted', refreshElement
      $scope.$on 'actionsResolved', refreshElement

      $scope.$on '$stateChangeSuccess', (event, state, params) ->
        return if state.name != 'mc.resource.show.property'
        $scope.property = params.property

      # init

      onElementUpdate($scope.element)

    ]
  }
]