angular.module('mc.core.ui.catalogueElementView', ['modelcatalogue.core.enhancersConf.catalogueElementEnhancer', 'modelcatalogue.core.enhancersConf.listReferenceEnhancer', 'modelcatalogue.core.enhancersConf.listEnhancer', 'mc.util.names', 'mc.util.messages', 'mc.core.ui.columns', 'mc.util.ui.actions', 'mc.util.ui.applicationTitle', 'ui.router', 'mc.core.ui.catalogueElementProperties', 'ngSanitize', 'mc.core.ui.messagesPanel', 'modelcatalogue.core.components.catalogueElementTreeview.model']).directive 'catalogueElementView',  [-> {
    restrict: 'E'
    replace: true
    scope:
      element: '='
      property: '=?'
      id: '@'
      displayOnly: '=?'

    templateUrl: '/mc/core/ui/catalogueElementView.html'

    controller: [
     '$scope', '$filter', '$q', '$timeout', '$state', 'enhance', 'names', 'columns', 'messages', '$element', '$rootScope', 'security', 'catalogueElementProperties', '$injector', 'applicationTitle', 'catalogue', 'catalogueElementResource', 'detailSections', 'rest', 'modelCatalogueApiRoot', 'actionRoleAccess',
     ($scope ,  $filter ,  $q ,  $timeout ,  $state ,  enhance ,  names ,  columns ,  messages ,  $element ,  $rootScope ,  security ,  catalogueElementProperties ,  $injector ,  applicationTitle ,  catalogue ,  catalogueElementResource ,  detailSections, rest, modelCatalogueApiRoot, actionRoleAccess) ->
      $scope.actionRoleAccess = actionRoleAccess
      showErrorsUsingMessages = (localMessages) ->
        (response) ->
          if response?.data and response.data.errors
            if angular.isString response.data.errors
              localMessages.error response.data.errors
            else
              for err in response.data.errors
                localMessages.error err.message

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

      tabsByName = {}

      $scope.property ?= $rootScope?.$stateParams?.property
      $scope.reports  = []
      $scope.messages = messages.createNewMessages()
      $scope.rest = rest
      $scope.modelCatalogueApiRoot = modelCatalogueApiRoot
      $scope.globalMessages = messages
      $scope.security = security




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

        return if oldProperty is newProperty and newProperty == $rootScope?.$stateParams?.property

        loadTab(newProperty)

        propCfg = catalogueElementProperties.getConfigurationFor("#{$scope.element.elementType}.#{newProperty}")
        page    = 1

        isTable = false
        if $scope.showTabs
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

      updateInlineEditHelperVariables = (element) ->
        $scope.copy = angular.copy(element)
        $scope.detailSections = detailSections.getAvailableViews(element)
        $scope.extAsMap = $scope.copy.ext?.asMap() ? {values: []}
        $scope.customMetadata = angular.copy($scope.copy?.ext ? {values: []})

        for section in $scope.detailSections
          for key in (section.keys ? [])
            $scope.customMetadata.remove(key)

        $scope.customMetadataKeys = []
        for value in $scope.customMetadata.values
          $scope.customMetadataKeys.push value.key


        return



      onElementUpdate = (element, oldEl) ->
        return if angular.equals element, oldEl

        updateInlineEditHelperVariables element

        applicationTitle "#{element.getLabel()}" if angular.isFunction(element?.getLabel)

        activeTabSet = false
        tabs = []

        for name, obj of element
          propertyConfiguration = catalogueElementProperties.getConfigurationFor("#{element.elementType}.#{name}")

          tabDefinition = getTabDefinition element, name, obj


          if not tabDefinition or not tabDefinition.name
            continue

          tabDefinition.hidden = propertyConfiguration.hidden(security)

          if tabDefinition.name == $scope.property
            tabDefinition.active = true
            $scope.$broadcast 'infiniteTableRedraw'
            activeTabSet = true

          tabs.push tabDefinition


        tabs = $filter('orderBy')(tabs, 'heading')

        tabDefinition = getTabDefinition element, 'properties', undefined

        propertyConfiguration = catalogueElementProperties.getConfigurationFor("#{element.elementType}.properties")
        unless not tabDefinition or not tabDefinition.name or propertyConfiguration.hidden(security)
          if 'properties' == $scope.property
            tabDefinition.active = true
            tabDefinition.hidden = propertyConfiguration.hidden(security)
            $scope.$broadcast 'infiniteTableRedraw'
            activeTabSet = true

          tabs.unshift tabDefinition

        tabsByName = {}
        for tab in tabs
          tabsByName[tab.name] = tab


        showTabs = false

        if not activeTabSet
          for tab in tabs
            if not tab.disabled and not tab.hidden
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
      $scope.detailSections = []
      $scope.select = (tab) ->
        nextState = if $state.includes 'mc' then 'mc.resource.show.property' else 'simple.resource.show.property'
        $state.go nextState, {property: tab.name}

      $scope.isTableSortable = (tab) ->
        return false unless $scope.element?.status == 'DRAFT'
        return false unless tab.value?.size > 1
        return false unless tab.value?.type
        return true

      $scope.reorder = (tab, $row, $current) -> tab.loader.reorder($row.row.element, $current?.row?.element).catch (reason) ->
        messages.error reason

      $scope.supportsInlineEdit = (editableForm) ->
        return security.hasRole('CURATOR') \
            and editableForm \
            and angular.isFunction($scope.element?.isInstanceOf) \
            and (\
              $scope.element?.isInstanceOf('dataModel') \
                or $scope.element?.isInstanceOf('enumeratedType')\
                or $scope.element?.isInstanceOf('asset')\
                or $scope.element?.isInstanceOf('dataClass')\
                or $scope.element?.isInstanceOf('dataType')\
                or $scope.element?.isInstanceOf('primitiveType')\
                or $scope.element?.isInstanceOf('referenceType')\
                or $scope.element?.isInstanceOf('measurementUnit')\
                or $scope.element?.isInstanceOf('dataElement')\
                or $scope.element?.isInstanceOf('validationRule')\
                or $scope.element?.isInstanceOf('tag')\
            )\
            and ($scope.element?.status == 'DRAFT' or security.hasRole('SUPERVISOR'))

      $scope.inlineUpdateElement = ->
        deferred = $q.defer()
        autoSavePromises = []
        if $scope.copy.ext
          $scope.copy.ext.updateFrom($scope.extAsMap)

          for key in $scope.customMetadataKeys
            $scope.copy.ext.remove(key)

          $scope.copy.ext.updateFrom($scope.customMetadata, true)

        $scope.messages.clearAllMessages()

        # for each detail section collect autoSave props
        # check autoSave property is string - save if so
        # replace string with newly created value
        # save update the final element
        angular.forEach $scope.detailSections, (detailSection) ->
          if detailSection.autoSave
            angular.forEach detailSection.autoSave, (value, key) ->
              if ($scope.copy[key] and angular.isString($scope.copy[key]))
                autoSavePromises.push catalogueElementResource(value).save(name: $scope.copy[key], dataModels: $scope.copy.dataModels).then (saved) ->
                  $scope.copy[key] = saved

        $q.all(autoSavePromises).then ->
          catalogueElementResource($scope.copy.elementType).update($scope.copy).then (updated) ->
            $scope.element.updateFrom(updated)

            updateInlineEditHelperVariables updated

            deferred.resolve()
            $timeout ->
              $scope.$broadcast 'redrawContextualActions'
          , (response) ->
            showErrorsUsingMessages($scope.messages)(response)
            deferred.reject("Invalid values")
        , (response) ->
          showErrorsUsingMessages($scope.messages)(response)
          deferred.reject("Invalid values")


        return deferred.promise


      refreshElement = ->
        if $scope.element
          $scope.element.refresh().then (refreshed)->
            return unless angular.isFunction($scope.element.updateFrom)
            $scope.element.updateFrom(refreshed)
            onElementUpdate($scope.element)

      isForCurrentElement = (data) -> data[1].link is $scope.element.link

      DEBOUNCE_TIME = 500

      $scope.$eventToObservable('catalogueElementUpdated').filter(isForCurrentElement).debounce(DEBOUNCE_TIME).subscribe refreshElement

      $scope.$on '$stateChangeSuccess', (event, state, params) ->
        return if state.name != 'mc.resource.show.property' and state.name != 'simple.resource.show.property'
        $scope.property = params.property
        if params.focused
          $scope.displayOnly = params.property
        else
          $scope.displayOnly = undefined


      # init
      onElementUpdate($scope.element)

      # watches
      $scope.$watch 'element', onElementUpdate
      $scope.$watch 'property', onPropertyUpdate
    ]
  }
]
