angular.module('mc.core.ui.catalogueElementView', ['mc.core.catalogueElementEnhancer', 'mc.core.listReferenceEnhancer', 'mc.core.listEnhancer', 'mc.util.names', 'mc.util.messages', 'mc.core.ui.columns', 'ui.router']).directive 'catalogueElementView',  [-> {
    restrict: 'E'
    replace: true
    scope:
      element: '='
      property: '=?'
      id: '@'

    templateUrl: 'modelcatalogue/core/ui/catalogueElementView.html'

    controller: ['$scope', '$log', '$filter', '$q', '$state', 'enhance', 'names', 'columns', 'messages', '$rootScope', 'catalogueElementResource', 'modelCatalogueApiRoot', ($scope, $log, $filter, $q, $state, enhance, names, columns, messages, $rootScope, catalogueElementResource) ->
      propExcludes     = ['version', 'name', 'description', 'incomingRelationships', 'outgoingRelationships', 'availableReports']
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

      onPropertyUpdate = (newProperty, oldProperty) ->
        page    = 1
        options = {}
        isTable = false
        if $scope.showTabs
          if newProperty
            $scope.naturalPropertyName = names.getNaturalName(newProperty)
            for tab in $scope.tabs
              tab.active = tab.name == newProperty
              if tab.active
                isTable = tab.type == 'decorated-list'
                if isTable and tab.value.total
                  page = tab.value.currentPage

          else
            for tab in $scope.tabs
              if tab.active
                $scope.property = tab.name
                isTable = tab.type == 'decorated-list'
                if isTable and tab.value.total
                  page = tab.value.currentPage
                break

        page = undefined if page == 1 or isNaN(page)
        options.location = "replace" if newProperty and not oldProperty
        $state.go 'mc.resource.show.property', {resource: names.getPropertyNameFromType($scope.element.elementType), id: $scope.element.id, property: newProperty, page: page}, options if $scope.element

      onElementUpdate = (element) ->
        resource = catalogueElementResource(element.elementType) if element and element.elementType

        activeTabSet     = false

        onPropertyUpdate($scope.property, $rootScope?.$stateParams?.property)

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


          if tabDefinition.name == 'history'
            tabDefinition.columns = [
              {header: "Version", value: 'versionNumber', class: 'col-md-1', show: true}
              {header: "Name", value: 'name', class: 'col-md-5', show: true}
              {header: "Model Catalogue Id", value: 'modelCatalogueId', class: 'col-md-6'}
            ]
          else if fn.itemType == 'org.modelcatalogue.core.Relationship'
            tabDefinition.actions.push {
              title:  'Remove'
              icon:   'remove'
              type:   'danger'
              action: (rel) ->
                deferred = $q.defer()
                messages.confirm('Removing Relationship', "Do you really want to remove relation '#{element.name} #{rel.type[rel.direction]} #{rel.relation.name}'?").then () ->
                    rel.remove().then ->
                      messages.success('Relationship removed!', "#{rel.relation.name} is no longer related to #{element.name}")
                      # reloads the table
                      deferred.resolve(true)
                    , (response) ->
                      if response.status == 404
                        messages.error('Error removing relationship', 'Relationship cannot be removed, it probably does not exist anymore. The table was refreshed to get the most up to date results.')
                        deferred.resolve(true)
                      else
                        messages.error('Error removing relationship', 'Relationship cannot be removed, see application logs for details')

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
            type:       'simple-object-editor'
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
            $scope.reports  = result.availableReports
        else
          $scope.reports    = tab.value?.availableReports

      $scope.createRelationship = () ->
        messages.prompt('Create Relationship', '', {type: 'new-relationship', element: $scope.element})

      $scope.canEdit = ->
        return false if not $scope.element
        messages.hasPromptFactory('edit-' + names.getPropertyNameFromType($scope.element.elementType))

      $scope.edit = ->
        return if not $scope.element or $scope.element.archived
        messages.prompt('Edit ' + $scope.element.elementTypeName, '', {type: 'edit-' + names.getPropertyNameFromType($scope.element.elementType), element: $scope.element}).then (updated)->
          $scope.element = updated

      # watches
      $scope.$watch 'element', onElementUpdate
      $scope.$watch 'property', onPropertyUpdate


      refreshElement = () ->
        if $scope.element
          $scope.element.refresh().then (refreshed)->
            $scope.element = refreshed

      $scope.$on 'catalogueElementCreated', refreshElement
      $scope.$on 'catalogueElementDeleted', refreshElement

      $scope.$on '$stateChangeSuccess', (event, state, params) ->
        return if state.name != 'mc.resource.show.property'
        $scope.property = params.property

      # init
      onElementUpdate($scope.element)
    ]
  }
]