angular.module('mc.core.ui.catalogueElementView', ['mc.core.catalogueElementEnhancer', 'mc.core.listReferenceEnhancer', 'mc.core.listEnhancer', 'mc.util.names', 'mc.util.messages', 'mc.core.ui.columns', 'mc.util.ui.actions', 'mc.util.ui.applicationTitle', 'ui.router', 'mc.core.ui.catalogueElementProperties', 'ngSanitize']).directive 'catalogueElementView',  [-> {
    restrict: 'E'
    replace: true
    scope:
      element: '='
      property: '=?'
      id: '@'

    templateUrl: 'modelcatalogue/core/ui/catalogueElementView.html'

    controller: ['$scope', '$filter', '$q', '$state', 'enhance', 'names', 'columns', 'messages', '$rootScope', 'catalogueElementResource', 'security', 'catalogueElementProperties', '$injector', 'applicationTitle', 'catalogue', ($scope, $filter, $q, $state, enhance, names, columns, messages, $rootScope, catalogueElementResource, security, catalogueElementProperties, $injector, applicationTitle, catalogue) ->
      updateFrom = (original, update) ->
        for originalKey of original
          if originalKey.indexOf('$') != 0 # keep the private fields such as number of children in tree view
            delete original[originalKey]

        for newKey of update
          original[newKey] = update[newKey]
        original

      propExcludes     = ['version', 'name', 'classifiedName', 'description', 'incomingRelationships', 'outgoingRelationships', 'relationships', 'availableReports', 'downloadUrl', 'archived', 'status', '__enhancedBy', 'parent', 'oldValue', 'newValue']
      listEnhancer    = enhance.getEnhancer('list')
      getPropertyVal  = (propertyName) ->
        (element) -> element[propertyName]

      getSortedMapPropertyVal = (propertyName) ->
        (element) ->
          for value in element.values
            if value.key == propertyName
              return value.value

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

        return if oldProperty is newProperty
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
              $scope.$broadcast 'infiniteTableRedraw'
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
        if($state.$current.name isnt "mc.resource.list") and $scope.element
          $state.go 'mc.resource.show.property', {resource: names.getPropertyNameFromType($scope.element.elementType), id: $scope.element.id, property: newProperty, page: page, q: $state.params.q}, options

      onElementUpdate = (element, oldEl) ->

        return if angular.equals element, oldEl

        applicationTitle "#{element.getLabel()}"

        resource = catalogueElementResource(element.elementType) if element and element.elementType

        activeTabSet = false

        tabs = []

        for name, fn of element when enhance.isEnhancedBy(fn, 'listReference')
          if name in propExcludes
            continue

          propertyConfiguration = catalogueElementProperties.getConfigurationFor("#{element.elementType}.#{name}")

          if propertyConfiguration.hidden(security)
            continue

          tabDefinition =
            heading:  propertyConfiguration.label
            value:    angular.extend(listEnhancer.createEmptyList(fn.itemType, fn.total), {base: fn.base})
            disabled: fn.total == 0
            loader:   fn
            type:     'decorated-list'
            columns:   propertyConfiguration.columns ? columns(fn.itemType)
            name:     name
            reports:  []


          if tabDefinition.name == $scope.property
            tabDefinition.active = true
            $scope.$broadcast 'infiniteTableRedraw'
            activeTabSet = true

          tabs.push tabDefinition


        for name, obj of element
          if name in propExcludes
            continue
          unless angular.isObject(obj) and !angular.isArray(obj) and (!enhance.isEnhanced(obj) or enhance.isEnhancedBy(obj, 'orderedMap'))
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
            type:       if security.hasRole('CURATOR') and element.status == 'DRAFT' then 'simple-object-editor' else 'properties-pane'
            isDirty:    ->
              if @value and enhance.isEnhancedBy(@value, 'orderedMap') and @original and enhance.isEnhancedBy(@original, 'orderedMap')
                return false if angular.equals(@value.values, @original.values)
                return false if @original.values.length == 0 and @value.values.length == 1 and not @value.values[0].value and not @value.values[0].key
              !angular.equals(@original, @value)
            reset:      -> @value = angular.copy @original
            update:     ->
              if not resource
                messages.error("Cannot update property #{names.getNaturalName(self.name)} of #{element.name}. See application logs for details.")
                return

              payload = {
                id: element.id
              }
              payload[@name] = angular.copy(@value)
              self = @
              resource.update(payload).then (updated) ->
                updateFrom($scope.element, updated)
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


          if obj?.type == 'orderedMap'
            for value in obj.values when not angular.isObject(value.value)
              tabDefinition.properties.push {
                label: value.key
                value: getSortedMapPropertyVal(value.key)
              }
          else
            for key, value of obj when not angular.isObject(value)
              tabDefinition.properties.push {
                label: key
                value: getPropertyVal(key)
              }

          if tabDefinition.name == $scope.property
            tabDefinition.active = true
            $scope.$broadcast 'infiniteTableRedraw'
            activeTabSet = true

          tabs.push tabDefinition


        tabs = $filter('orderBy')(tabs, 'heading')

        if enhance.isEnhancedBy(element, 'change')
          tabDefinition =
            heading:    'Properties'
            name:       'properties'
            value:      element
            properties: [
              {label: 'Parent Change', value: getPropertyVal('parent')}
              {label: 'Change Type', value: getPropertyVal('type')}
              {label: 'Changed Element', value: getPropertyVal('changed')}
              {label: 'Root Element', value: getPropertyVal('latestVersion')}
              {label: 'Author', value: getPropertyVal('author')}
              {label: 'Undone', value: getPropertyVal('undone')}
            ]
            type:       'properties-pane'

          if tabDefinition.name == $scope.property
            tabDefinition.active = true
            $scope.$broadcast 'infiniteTableRedraw'
            activeTabSet = true

          tabs.unshift tabDefinition
        else if enhance.isEnhancedBy(element, 'catalogueElement')
          newProperties = []
          for prop in element.getUpdatableProperties()
            obj = element[prop]
            if prop in propExcludes or angular.isFunction(obj)
              continue
            if enhance.isEnhancedBy(obj, 'listReference')
              continue
            if enhance.isEnhancedBy(obj, 'orderedMap')
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
            $scope.$broadcast 'infiniteTableRedraw'
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
              $scope.$broadcast 'infiniteTableRedraw'
              $scope.property = tab.name
              showTabs = true
              break
        else
          showTabs = true
          loadTab($scope.property)

        $scope.tabs = tabs
        $scope.showTabs = showTabs

      $scope.getDeprecationWarning = ->
        return catalogue.getDeprecationWarning($scope.element.elementType)($scope.element) if $scope.element and $scope.element.elementType

      $scope.tabs   = []
      $scope.select = (tab) ->
        $scope.property = tab.name
        $scope.$broadcast 'infiniteTableRedraw'

      $scope.isTableSortable = (tab) ->
        return false unless tab.value?.size > 1
        return false unless tab.value?.type
        return false if tab.value.type.bidirectional
        return true

      $scope.reorder = (tab, $row, $current) ->
        tab.loader.reorder($row.row.element, $current?.row?.element)

      refreshElement = () ->
        if $scope.element
          $scope.element.refresh().then (refreshed)->
            updateFrom($scope.element, refreshed)
            onElementUpdate($scope.element)

      $scope.$on 'userLoggedIn', refreshElement
      $scope.$on 'userLoggedIn', refreshElement
      $scope.$on 'userLoggedOut', refreshElement
      $scope.$on 'catalogueElementCreated', refreshElement
      $scope.$on 'catalogueElementDeleted', refreshElement
      $scope.$on 'catalogueElementUpdated', refreshElement
      $scope.$on 'newVersionCreated', (ignored, element) ->
        if($state.$current.name isnt "mc.resource.list")
          $state.go 'mc.resource.show.property', {resource: names.getPropertyNameFromType(element.elementType), id: element.id, property: 'history', page: undefined, q: undefined}

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