angular.module('mc.core.ui.catalogueElementView', ['mc.core.catalogueElementEnhancer', 'mc.core.listReferenceEnhancer', 'mc.core.listEnhancer', 'mc.util.names', 'mc.util.messages', 'mc.core.ui.columns', 'mc.util.ui.actions', 'mc.util.ui.applicationTitle', 'ui.router', 'mc.core.ui.catalogueElementProperties', 'ngSanitize']).directive 'catalogueElementView',  [-> {
    restrict: 'E'
    replace: true
    scope:
      element: '='
      property: '=?'
      id: '@'

    templateUrl: 'modelcatalogue/core/ui/catalogueElementView.html'

    controller: ['$scope', '$filter', '$q', '$state', 'enhance', 'names', 'columns', 'messages', '$rootScope', 'catalogueElementResource', 'security', 'catalogueElementProperties', '$injector', 'applicationTitle', ($scope, $filter, $q, $state, enhance, names, columns, messages, $rootScope, catalogueElementResource, security, catalogueElementProperties, $injector, applicationTitle) ->
      propExcludes     = ['version', 'name', 'classifiedName', 'description', 'incomingRelationships', 'outgoingRelationships', 'availableReports', 'downloadUrl', 'archived', 'status']
      listEnhancer    = enhance.getEnhancer('list')
      getPropertyVal  = (propertyName) ->
        (element) -> element[propertyName]

      getObjectSize   = (object) ->
        size = 0
        angular.forEach object, () ->
          size++
        size

      tabsByName = {}

      $scope.property ?= $rootScope?.$stateParams?.property
      $scope.reports  = []


      loadTab = (property) ->
        tab = tabsByName[property]

        applicationTitle "#{if tab and tab.heading then tab.heading else names.getNaturalName(property)} of #{$scope.element.getLabel()}"

        if not tab?.loader?
          $scope.reports  = []
          return

        if !tab.disabled and (tab.value.empty or tab.search != $state.params.q)
          promise = null

          if tab.value.empty or tab.search != $state.params.q
            if $state.params.q
              promise = tab.loader 'search', {search: $state.params.q}
            else
              if not tab.promise
                promise = tab.loader()
                tab.promise = promise
              else
                promise = tab.promise
          else
            promise = $q.when tab.value

          tab.search = $state.params.q

          promise.then (result) ->
            tab.value       = result
            $scope.reports  = result.availableReports
        else
          $scope.reports    = tab.value?.availableReports


      onPropertyUpdate = (newProperty, oldProperty) ->
        loadTab(newProperty)

        propCfg = catalogueElementProperties.getConfigurationFor("#{$scope.element.elementType}.#{newProperty}")
        page    = 1
        options = {}
        isTable = false
        if $scope.showTabs and not propCfg.hidden(security)
          if newProperty

            $scope.naturalPropertyName = propCfg.label

            $rootScope.$$searchContext = if tabsByName[newProperty]?.loader && tabsByName[newProperty]?.search then propCfg.label else undefined

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
        else
          $rootScope.$$searchContext = undefined

        page = undefined if page == 1 or isNaN(page)
        options.location = "replace" if newProperty and not oldProperty
        $state.go 'mc.resource.show.property', {resource: names.getPropertyNameFromType($scope.element.elementType), id: $scope.element.id, property: newProperty, page: page, q: $state.params.q}, options if $scope.element

      onElementUpdate = (element, oldEl) ->
        return if angular.equals element, oldEl

        applicationTitle "#{element.getLabel()}"

        resource = catalogueElementResource(element.elementType) if element and element.elementType

        activeTabSet = false

        onPropertyUpdate($scope.property, $rootScope?.$stateParams?.property)

        tabs = []

        for name, fn of element when enhance.isEnhancedBy(fn, 'listReference')
          if name in propExcludes
            continue

          propertyConfiguration = catalogueElementProperties.getConfigurationFor("#{element.elementType}.#{name}")

          if propertyConfiguration.hidden(security)
            continue

          tabDefinition =
            heading:  propertyConfiguration.label
            value:    listEnhancer.createEmptyList(fn.itemType, fn.total)
            disabled: fn.total == 0
            loader:   fn
            type:     'decorated-list'
            columns:   propertyConfiguration.columns ? columns(fn.itemType)
            actions:   $injector.invoke propertyConfiguration.actions ? []
            name:     name
            reports:  []


          if fn.itemType == 'org.modelcatalogue.core.Relationship' and security.hasRole('CURATOR')
            tabDefinition.actions.push {
              icon:   'edit'
              type:   'primary'
              action: (rel) ->
                args = {relationshipType: rel.type, direction: rel.direction, type: 'new-relationship', update: true, element: element, relation: rel.relation, metadata: angular.copy(rel.ext)}
                messages.prompt('Update Relationship', '', args).then (updated)->
                  rel.ext = updated.ext
            }
            tabDefinition.actions.push {
              icon:   'remove'
              type:   'danger'
              action: (rel) ->
                deferred = $q.defer()
                messages.confirm('Remove Relationship', "Do you really want to remove relation '#{element.name} #{rel.type[rel.direction]} #{rel.relation.name}'?").then () ->
                    rel.remove().then ->
                      messages.success('Relationship removed!', "#{rel.relation.name} is no longer related to #{element.name}")
                      # reloads the table
                      deferred.resolve(true)
                    , (response) ->
                      if response.data?.errors
                        if angular.isString response.data.errors
                          messages.error response.data.errors
                        else
                          for err in response.data.errors
                            messages.error err.message
                      else if response.status == 404
                        messages.error('Error removing relationship', 'Relationship cannot be removed, it probably does not exist anymore. The table was refreshed to get the most up to date results.')
                        deferred.resolve(true)
                      else
                        messages.error('Error removing relationship', 'Relationship cannot be removed, see application logs for details')

                deferred.promise
            }

          if fn.itemType == 'org.modelcatalogue.core.Mapping'
            tabDefinition.actions.push {
              icon:   'arrow-right'
              type:   'primary'
              action: (mapping) ->
                messages.prompt('', '', {type: 'convert-with-value-domain', source: element, destination: mapping.destination})
            }
            if security.hasRole('CURATOR')
              tabDefinition.actions.push {
                icon:   'edit'
                type:   'primary'
                action: (mapping) ->
                  args = {type: 'new-mapping', update: true, element: element, mapping: mapping}
                  messages.prompt('Update Mapping', '', args).then (updated)->
                    angular.extend mapping, updated
              }
              tabDefinition.actions.push {
                icon:   'remove'
                type:   'danger'
                action: (mapping) ->
                  deferred = $q.defer()
                  messages.confirm('Remove Mapping', "Do you really want to remove mapping from #{element.name} to #{mapping.destination.name}?").then ->
                      element.mappings.remove(mapping.destination.id, {}).then ->
                        messages.success('Mapping removed!', "#{mapping.destination.name} is no longer related to #{element.name}")
                        # reloads the table
                        deferred.resolve(true)
                      , (response) ->
                        if response.data?.errors
                          if angular.isString response.data.errors
                            messages.error response.data.errors
                          else
                            for err in response.data.errors
                              messages.error err.message
                        else if response.status == 404
                          messages.error('Error removing mapping', 'Mapping cannot be removed, it probably does not exist anymore. The table was refreshed to get the most up to date results.')
                          deferred.resolve(true)
                        else
                          messages.error('Error removing mapping', 'Mapping cannot be removed, see application logs for details')

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

          propertyConfiguration = catalogueElementProperties.getConfigurationFor("#{element.elementType}.#{name}")

          if propertyConfiguration.hidden(security)
            continue

          tabDefinition =
            name:       name
            heading:    propertyConfiguration.label
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
              ,  (response) ->
                  if response.data.errors
                    if angular.isString response.data.errors
                      messages.error response.data.errors
                    else
                      for err in response.data.errors
                        messages.error err.message
                  else
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

        tabsByName = {}
        for tab in tabs
          tabsByName[tab.name] = tab


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
          loadTab($scope.property)

        $scope.tabs = tabs
        $scope.showTabs = showTabs

      $scope.tabs   = []
      $scope.select = (tab) ->
        if $state.current.abstract
          $scope.property = tab.name
          loadTab(tab.property)
        else
          $state.go '.', {property: tab.name, q: tab.search}


      refreshElement = () ->
        if $scope.element
          $scope.element.refresh().then (refreshed)->
            $scope.element = refreshed

      $scope.$on 'catalogueElementCreated', refreshElement
      $scope.$on 'catalogueElementDeleted', refreshElement

      $rootScope.$on 'userLoggedIn', refreshElement
      $rootScope.$on 'userLoggedOut', refreshElement


      $scope.$on '$stateChangeSuccess', (event, state, params) ->
        return if state.name != 'mc.resource.show.property'
        $scope.property = params.property

      # init
      onElementUpdate($scope.element)

      # watches
      $scope.$watch 'element', onElementUpdate
      $scope.$watch 'property', onPropertyUpdate
    ]
  }
]