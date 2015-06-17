angular.module('mc.core.ui.catalogueElementView', ['mc.core.catalogueElementEnhancer', 'mc.core.listReferenceEnhancer', 'mc.core.listEnhancer', 'mc.util.names', 'mc.util.messages', 'mc.core.ui.columns', 'mc.util.ui.actions', 'mc.util.ui.applicationTitle', 'ui.router', 'mc.core.ui.catalogueElementProperties', 'ngSanitize']).directive 'catalogueElementView',  [-> {
    restrict: 'E'
    replace: true
    scope:
      element: '='
      property: '=?'
      id: '@'

    templateUrl: 'modelcatalogue/core/ui/catalogueElementView.html'

    controller: ['$scope', '$filter', '$q', '$state', 'enhance', 'names', 'columns', 'messages', '$rootScope', 'catalogueElementResource', 'security', 'catalogueElementProperties', '$injector', 'applicationTitle', 'catalogue', ($scope, $filter, $q, $state, enhance, names, columns, messages, $rootScope, catalogueElementResource, security, catalogueElementProperties, $injector, applicationTitle, catalogue) ->
      getTabDefinition = (element, name, value) ->
        possibilities = ["#{element.elementType}.#{name}"]
        if enhance.isEnhanced(value)
          possibilities = possibilities.concat("enhanced:#{enhancer}" for enhancer in value.getEnhancedBy())
          possibilities.push 'type:array'     if angular.isArray(value)
          possibilities.push 'type:function'  if angular.isFunction(value)
          possibilities.push 'type:date'      if angular.isDate(value)
          possibilities.push 'type:string'    if angular.isString(value)
          possibilities.push 'type:object'    if angular.isObject(value)
        for possibility in possibilities
          factory = catalogueElementProperties.getConfigurationFor(possibility)?.tabDefinition
          continue unless factory
          result = $injector.invoke(factory, undefined, $element: element, $name: name, $value: value, $scope: $scope)
          return result if result

      updateFrom = (original, update) ->
        for originalKey of original
          if originalKey.indexOf('$') != 0 # keep the private fields such as number of children in tree view
            delete original[originalKey]

        for newKey of update
          original[newKey] = update[newKey]
        original


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

        applicationTitle "#{element.getLabel()}" if angular.isFunction(element?.getLabel)

        activeTabSet = false
        tabs = []

        for name, obj of element
          propertyConfiguration = catalogueElementProperties.getConfigurationFor("#{element.elementType}.#{name}")

          if propertyConfiguration.hidden(security)
            continue

          tabDefinition = getTabDefinition element, name, obj

          if not tabDefinition or not tabDefinition.name
            continue

          if tabDefinition.name == $scope.property
            tabDefinition.active = true
            $scope.$broadcast 'infiniteTableRedraw'
            activeTabSet = true

          tabs.push tabDefinition


        tabs = $filter('orderBy')(tabs, 'heading')

        tabDefinition = getTabDefinition element, 'properties', undefined

        unless not tabDefinition or not tabDefinition.name
          if 'properties' == $scope.property
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

      refreshElement = ->
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