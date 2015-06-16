angular.module('mc.core.ui.bs.actions', ['mc.util.ui.actions']).config ['actionsProvider', 'names', (actionsProvider, names)->
  ROLE_ACTION_ACTION = 'action'


  updateFrom = (original, update) ->
    for originalKey of original
      if originalKey.indexOf('$') != 0 # keep the private fields such as number of children in tree view
        delete original[originalKey]

    for newKey of update
      original[newKey] = update[newKey]
    original


  showErrorsUsingMessages = (messages) ->
    (response) ->
      if response?.data and response.data.errors
        if angular.isString response.data.errors
          messages.error response.data.errors
        else
          for err in response.data.errors
            messages.error err.message

  actionsProvider.registerActionInRole 'create-catalogue-element', actionsProvider.ROLE_LIST_ACTION, ['$scope', 'names', 'security', 'messages', '$state', '$log', ($scope, names, security, messages, $state, $log) ->
    return undefined if not security.hasRole('CURATOR')
    return undefined if not $scope.resource
    return undefined if $scope.resource == 'batch'
    return undefined if not messages.hasPromptFactory('create-' + $scope.resource) and not messages.hasPromptFactory('edit-' + $scope.resource)

    {
    position:   100
    label:      "New #{names.getNaturalName($scope.resource)}"
    icon:       'glyphicon glyphicon-plus-sign'
    type:       'success'
    action:     ->
      args      = {create: ($scope.resource)}
      args.type = if messages.hasPromptFactory('create-' + $scope.resource) then "create-#{$scope.resource}" else "edit-#{$scope.resource}"

      if $scope.resource == 'model' and $scope.element and $scope.elementSelectedInTree
        args.parent = $scope.element

      security.requireRole('CURATOR')
      .then ->
        messages.prompt('Create ' + names.getNaturalName($scope.resource), '', args).then ->
          if $scope.resource == 'model' and $state.current.name == 'mc.resource.list'
            # reload in draft mode
            $state.go '.', {status: 'draft'}, {reload: true}
      , (errors)->
        $log.error errors
        messages.error('You don\'t have rights to create new elements')
    }
  ]

  actionsProvider.registerActionInRole 'new-import', actionsProvider.ROLE_LIST_ACTION, [
    '$scope', 'names','security', '$state',
    ($scope ,  names , security ,  $state ) ->
      return undefined if not security.hasRole('CURATOR')
      return undefined if $state.current.name != 'mc.resource.list'
      return undefined if $scope.resource != 'asset'

      {
        position: 100
        label: "Import"
        icon: 'fa fa-cloud-upload'
        type: 'success'
      }
  ]

  loincImport = ['$scope', 'messages', ($scope, messages) -> {
    label: "Import Loinc"
    action: ->
      messages.prompt('Import Loinc File', '', type: 'new-loinc-import')
  }]
  actionsProvider.registerChildAction 'new-import', 'import-loinc', loincImport
  actionsProvider.registerActionInRole 'global-import-loinc', actionsProvider.ROLE_GLOBAL_ACTION, loincImport

  excelImport = ['$scope', 'messages', ($scope, messages) -> {
    label:  "Import Excel"
    action: ->
      messages.prompt('Import Excel File', '', type: 'new-excel-import')
  }]
  actionsProvider.registerChildAction 'new-import', 'import-excel', excelImport
  actionsProvider.registerActionInRole 'global-import-excel', actionsProvider.ROLE_GLOBAL_ACTION, excelImport

  oboImport = ['$scope', 'messages', ($scope, messages) -> {
    label: "Import OBO"
    action: ->
      messages.prompt('Import OBO File', '', type: 'new-obo-import')
  }]
  actionsProvider.registerChildAction 'new-import', 'import-obo', oboImport
  actionsProvider.registerActionInRole 'global-import-obo', actionsProvider.ROLE_GLOBAL_ACTION, oboImport


  xsdImport = ['$scope', 'messages', ($scope, messages) -> {
    label: "Import XSD"
    action: ->
      messages.prompt('Import XSD File', '', type: 'new-xsd-import')
  }]
  actionsProvider.registerChildAction 'new-import', 'import-xsd', xsdImport
  actionsProvider.registerActionInRole 'global-import-xsd', actionsProvider.ROLE_GLOBAL_ACTION, xsdImport

  umlImport = ['$scope', 'messages', ($scope, messages) -> {
    label: "Import Star Uml"
    action: ->
      messages.prompt('Import Star Uml File', '', type: 'new-umlj-import')
  }]
  actionsProvider.registerChildAction 'new-import', 'import-umlj', umlImport
  actionsProvider.registerActionInRole 'global-import-uml', actionsProvider.ROLE_GLOBAL_ACTION, umlImport

  mcImport = ['$scope', 'messages', ($scope, messages) -> {
    label: "Import MC"
    action: ->
      messages.prompt('Import Model Catalogue DSL File', '', type: 'new-mc-import')
  }]
  actionsProvider.registerChildAction 'new-import', 'import-mc', mcImport
  actionsProvider.registerActionInRole 'global-import-mc', actionsProvider.ROLE_GLOBAL_ACTION, mcImport


  xmlImport = ['$scope', 'messages', ($scope, messages) -> {
    label: "Import Catalogue XML"
    action: ->
      messages.prompt('Import Model Catalogue XML File', '', type: 'new-catalogue-xml-import')
  }]
  actionsProvider.registerChildAction 'new-import', 'import-catalogue-xml', xmlImport
  actionsProvider.registerActionInRole 'global-import-xml', actionsProvider.ROLE_GLOBAL_ACTION, xmlImport




  actionsProvider.registerActionInRole 'favorite-element', actionsProvider.ROLE_ITEM_ACTION, ['$scope', 'messages', '$state', 'security', 'catalogueElementResource', 'modelCatalogueApiRoot', 'enhance', 'rest', ($scope, messages, $state, security, catalogueElementResource, modelCatalogueApiRoot, enhance, rest) ->
    elementPresent = $scope.element and angular.isFunction($scope.element.getResourceName) and angular.isFunction($scope.element.getElementTypeName) and angular.isFunction($scope.element.isInstanceOf) and $scope.element.isInstanceOf('catalogueElement')

    return undefined if not elementPresent
    return undefined if not security.getCurrentUser()?.id

    action =
      position: -200
      label: ''
      icon: 'fa fa-star'
      type: 'primary'
      action: ->
        catalogueElementResource('user').get(security.getCurrentUser()?.id).then (user) ->
          favourite = $scope.element.favourite
          url = "#{modelCatalogueApiRoot}#{user.link}/outgoing/favourite"
          enhance(rest(url: url, method: (if favourite then 'DELETE' else 'POST'), data: $scope.element)).then (relation) ->
            messages.success(if favourite then "#{$scope.element.getLabel()} has been removed from favorites" else "#{$scope.element.getLabel()} has been added to favorites")
            $scope.element.favourite = not favourite
            if favourite
              $scope.$broadcast 'catalogueElementDeleted', $scope.element, relation, url
            else
              $scope.$broadcast 'catalogueElementCreated', relation, url, $scope.element

            relation

    $scope.$watch 'element.favourite', (favourite) ->
      if favourite
        action.active = true
        action.icon   = 'fa fa-star-o'
      else
        action.active = false
        action.icon   = 'fa fa-star'

    action
  ]


  actionsProvider.registerActionInRoles 'favorite-element-in-header', [actionsProvider.ROLE_LIST_HEADER_ACTION, actionsProvider.ROLE_LIST_FOOTER_ACTION], ['$scope', 'messages', '$state', 'security', 'catalogueElementResource', 'modelCatalogueApiRoot', 'enhance', 'rest', ($scope, messages, $state, security, catalogueElementResource, modelCatalogueApiRoot, enhance, rest) ->
    return undefined if not $scope.list
    return undefined if not $scope.list.base
    return undefined if $scope.list.base.indexOf('/outgoing/favourite') == -1
    return undefined if not security.getCurrentUser()?.id

    {
      position:   200
      label:      'Add to Favourites'
      icon:       'fa fa-plus-circle'
      type:       'success'
      action: ->
        messages.prompt("Add to Favourites", "Please, select which element should be added to the favourite ones", {type: 'catalogue-element', resource: 'catalogueElement'}).then (element)->
          catalogueElementResource('user').get(security.getCurrentUser()?.id).then (user) ->
            url = "#{modelCatalogueApiRoot}#{user.link}/outgoing/favourite"
            enhance(rest(url: url, method: 'POST', data: element)).then (relation) ->
              $scope.$broadcast 'catalogueElementCreated', relation, url, element
              messages.success "#{element.getLabel()} has been added to favorites"
              relation

    }
  ]

  actionsProvider.registerActionInRole 'archive-batch', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', 'enhance', 'rest', 'modelCatalogueApiRoot', ($rootScope, $scope, messages, names, security, enhance, rest, modelCatalogueApiRoot) ->
    return undefined unless $scope.element and angular.isFunction($scope.element.isInstanceOf) and $scope.element.isInstanceOf('batch') or $scope.batch
    return undefined if not security.hasRole('CURATOR')

    action = {
      position:   150
      label:      'Archive'
      icon:       'glyphicon glyphicon-compressed'
      type:       'danger'
      action:     ->
        batch = $scope.batch ? $scope.element
        messages.confirm("Do you want to archive batch #{batch.name} ?", "The batch #{batch.name} will be archived").then ->
          enhance(rest(url: "#{modelCatalogueApiRoot}#{batch.link}/archive", method: 'POST')).then (archived) ->
            updateFroms batch, archived
          , showErrorsUsingMessages(messages)
    }

    updateAction = ->
      action.disabled = ($scope.batch ? $scope.element).archived

    $scope.$watch 'batch.archived', updateAction
    $scope.$watch 'element.archived', updateAction

    action
  ]


  actionsProvider.registerActionInRoles 'create-new-relationship-in-header', [actionsProvider.ROLE_LIST_HEADER_ACTION, actionsProvider.ROLE_LIST_FOOTER_ACTION], ['$scope', 'messages', 'names', 'security', 'catalogue', ($scope, messages, names, security, catalogue) ->
    return undefined if not $scope.list
    return undefined if not $scope.list.base
    return undefined if not catalogue.isInstanceOf($scope.list.itemType, 'relationship')
    return undefined if not $scope.$parent
    return undefined if not $scope.$parent.element
    return undefined if not security.hasRole('CURATOR')

    direction = if $scope.list.base?.indexOf('/incoming/') > -1 then 'destinationToSource' else 'sourceToDestination'
    relationshipType = $scope.list.base.substring($scope.list.base.lastIndexOf('/') + 1)

    action = {
      position:   200
      label:      'Add'
      icon:       'fa fa-plus-circle'
      type:       'success'
      action:     ->
        messages.prompt('Create Relationship', '', {type: 'create-new-relationship', element: $scope.$parent.element, direction: direction, relationshipTypeName: relationshipType}).catch showErrorsUsingMessages(messages)
    }

    updateAction = ->
      action.disabled = $scope.$parent.element.archived

    $scope.$parent.$watch 'element.status', updateAction
    $scope.$parent.$watch 'element.archived', updateAction

    action
  ]

  actionsProvider.registerActionInRoles 'create-new-mapping-in-header',  [actionsProvider.ROLE_LIST_HEADER_ACTION, actionsProvider.ROLE_LIST_FOOTER_ACTION], ['$scope', 'messages', 'names', 'security', 'catalogue', ($scope, messages, names, security, catalogue) ->
    return undefined if not $scope.$parent.element
    return undefined if not $scope.$parent.element.hasOwnProperty('mappings')
    return undefined if not security.hasRole('CURATOR')
    return undefined if not $scope.list
    return undefined if not catalogue.isInstanceOf($scope.list.itemType, 'mapping')

    {
    position:   300
    label:      'Add'
    icon:       'fa fa-plus-circle'
    type:       'success'
    action:     ->
      messages.prompt('Create new mapping for ' + $scope.$parent.element.name, '', {type: 'new-mapping', element: $scope.$parent.element}).catch showErrorsUsingMessages(messages)
    }
  ]

  actionsProvider.registerActionInRole 'transform-csv', actionsProvider.ROLE_ITEM_ACTION, [ '$scope', 'messages', 'security', ($scope, messages, security) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction $scope.element.isInstanceOf
    return undefined if not $scope.element.isInstanceOf('csvTransformation')
    return undefined if not security.isUserLoggedIn()

    {
      position:   0
      label:      'Transform'
      icon:       'fa fa-long-arrow-right'
      type:       'primary'
      action:     ->
        messages.prompt('Transform CSV File', '', {type: 'transform-csv-file', element: $scope.element})

    }
  ]

  actionsProvider.registerActionInRole 'refresh-asset', actionsProvider.ROLE_ITEM_ACTION, [ '$scope', '$rootScope', 'catalogueElementResource', ($scope, $rootScope, catalogueElementResource) ->
    return undefined if $scope.element?.elementType != 'org.modelcatalogue.core.Asset'
    return undefined if $scope.element.status != 'PENDING'

    {
      position:   -100
      label:      ''
      icon:       'glyphicon glyphicon-refresh'
      type:       'primary'
      action:     ->
        catalogueElementResource($scope.element.elementType).get($scope.element.id).then (refreshed) ->
          updateFrom $scope.element, refreshed
          $rootScope.$broadcast 'redrawContextualActions'
          $rootScope.$broadcast 'catalogueElementUpdated', refreshed

    }
  ]

  actionsProvider.registerActionInRole 'generate-merge-models', actionsProvider.ROLE_LIST_ACTION, ['$scope', 'security', 'catalogue', 'modelCatalogueApiRoot', 'enhance', 'rest', 'messages', '$state', ($scope, security, catalogue, modelCatalogueApiRoot, enhance, rest, messages, $state)->
    return undefined unless security.isUserLoggedIn()
    return undefined unless $scope.list
    return undefined unless catalogue.isInstanceOf($scope.list.itemType, 'batch')
    {
      position:   100
      label:      'Generate Suggestions'
      icon:       'fa fa-flash'
      type:       'primary'
      action: ->
        messages.prompt('Generate Suggestions', "Suggestions to optimalize catalogue will be generated. This may take a long time depending on complexity of the catalogue. You can rerun the action later to clean all resolved batches generated by this action.", {type: 'generate-suggestions'}).then ->
          $state.go('.', {page: undefined}, {reload: true})

    }
  ]

  actionsProvider.registerActionInRole 'refresh-batches', actionsProvider.ROLE_LIST_ACTION, ['$state', '$scope', 'security', 'catalogue', ($state, $scope, security, catalogue)->
    return undefined unless security.isUserLoggedIn()
    return undefined unless $scope.list
    return undefined unless catalogue.isInstanceOf($scope.list.itemType, 'batch')
    {
    position:   0
    label:      'Refresh'
    icon:       'fa fa-refresh'
    type:       'primary'
    action: ->
      $state.go('.', {page: undefined}, {reload: true})
    }
  ]

  actionsProvider.registerActionInRoles 'export', [actionsProvider.ROLE_LIST_ACTION, actionsProvider.ROLE_ITEM_ACTION, actionsProvider.ROLE_LIST_HEADER_ACTION], ['$scope', 'security', ($scope, security)->
    return undefined unless security.hasRole('VIEWER')
    return undefined unless $scope.list or $scope.element
    if $scope.list
      return undefined if $scope.resource == 'import'
    if $scope.element
      return undefined if not angular.isFunction $scope.element.isInstanceOf
      return undefined if $scope.element.isInstanceOf 'dataImport'
    {
      position:   1000
      label:      'Export'
      icon:       'glyphicon glyphicon-download-alt'
      type:       'primary'
      expandToLeft: true
    }
  ]

  generateReports = ($scope, $window, enhance, rest) ->
    (reports = []) ->
      for report in reports
        {
          label:  report.title
          url:    report.url
          type:   report.type
          action: ->
            if @type == 'LINK'
              $window.open(@url, '_blank')
            else enhance(rest(method: 'GET', url: @url)).then (result) ->
              result.show()
            return true
        }

  actionsProvider.registerChildActionInRole 'export', 'catalogue-element-export-specific-reports', actionsProvider.ROLE_ITEM_ACTION, ['$scope', '$window', 'enhance', 'rest', ($scope, $window, enhance, rest) ->
    return undefined if not $scope.element

    {
    position:   1000
    label:      "#{$scope.element.name} Reports"
    disabled:   not $scope.element?.availableReports?.length
    generator:  (action) ->
      action.createActionsFrom 'element.availableReports', generateReports($scope, $window, enhance, rest)
    }
  ]

  actionsProvider.registerChildAction 'export', 'generic-reports', ['$scope', '$window', 'enhance', 'rest', ($scope, $window, enhance, rest) ->
    {
    position:   2000
    label:      "Other Reports"
    disabled:   not $scope.reports?.length
    generator: (action) ->
      action.createActionsFrom 'reports', generateReports($scope, $window, enhance, rest)
    }
  ]

  actionsProvider.registerChildAction 'export', 'list-exports-current', actionsProvider.ROLE_LIST_ACTION, ['$scope', '$window', 'enhance', 'rest', ($scope, $window, enhance, rest) ->
    return undefined if not $scope.list?

    {
    position:   5000
    label:      "Current Reports"
    disabled:   not $scope.list.availableReports?.length
    generator:  (action) ->
      action.createActionsFrom 'list.availableReports', generateReports($scope, $window, enhance, rest)
    }
  ]

  actionsProvider.registerActionInRole 'switch-status', actionsProvider.ROLE_LIST_ACTION, ['$state', '$scope', '$stateParams', 'catalogue', 'security', ($state, $scope, $stateParams, catalogue, security) ->
    return undefined unless security.hasRole('VIEWER')
    return undefined unless $state.current.name == 'mc.resource.list'
    return undefined unless catalogue.isInstanceOf($stateParams.resource, 'catalogueElement')

    {
    abstract: true
    position: 500

    type:     (->
      return 'info'     if $stateParams.status == 'draft'
      return 'warning'  if $stateParams.status == 'pending'
      return 'primary'
    )()
    icon:     (->
      return 'glyphicon glyphicon-pencil'   if $stateParams.status == 'draft'
      return 'glyphicon glyphicon-time'     if $stateParams.status == 'pending'
      return 'glyphicon glyphicon-ok'
    )()
    label:    (->
      return 'Draft'    if $stateParams.status == 'draft'
      return 'Pending'  if $stateParams.status == 'pending'
      return 'Finalized'
    )()
    }
  ]

  actionsProvider.registerChildAction 'switch-status', 'switch-status-finalized', ['$state', '$stateParams', ($state, $stateParams) ->
    {
    position:   300
    label:      "Finalized"
    icon:       'glyphicon glyphicon-ok'
    type:       'primary'
    active:     !$stateParams.status or $stateParams.status == 'finalized'
    action:     ->
      newParams = angular.copy($stateParams)
      newParams.status = undefined
      $state.go 'mc.resource.list', newParams
    }
  ]

  actionsProvider.registerChildAction 'switch-status', 'switch-status-pending', ['$state', '$stateParams', ($state, $stateParams) ->
    {
      position:   200
      label:      "Pending"
      icon:       'glyphicon glyphicon-time'
      type:       'warning'
      active:     $stateParams.status == 'pending'
      action:     ->
        newParams = angular.copy($stateParams)
        newParams.status = 'pending'
        $state.go 'mc.resource.list', newParams
    }
  ]

  actionsProvider.registerChildAction 'switch-status', 'switch-status-draft', ['$state', '$stateParams', ($state, $stateParams) ->
    {
    position:   100
    label:      "Draft"
    icon:       'glyphicon glyphicon-pencil'
    type:       'info'
    active:     $stateParams.status == 'draft'
    action:     ->
      newParams = angular.copy($stateParams)
      newParams.status = 'draft'
      $state.go 'mc.resource.list', newParams
    }
  ]


  actionsProvider.registerActionInRole 'switch-archived-batches', actionsProvider.ROLE_LIST_ACTION, ['$state', '$scope', '$stateParams', ($state, $scope, $stateParams) ->
    return undefined unless $state.current.name == 'mc.resource.list' and $scope.list and $stateParams.resource == 'batch'

    {
    abstract: true
    position: 500

    type:     (->
      return 'info'        if $stateParams.status == 'archived'
      return 'glyphicon glyphicon-ok'
    )()
    label:    (->
      return 'Archived'    if $stateParams.status == 'archived'
      return 'Active'
    )()
    }
  ]

  actionsProvider.registerChildAction 'switch-archived-batches', 'switch-archived-batches-active', ['$state', '$stateParams', ($state, $stateParams) ->
    {
    position:   300
    label:      "Active"
    icon:       'glyphicon glyphicon-ok'
    type:       'primary'
    active:     !$stateParams.status
    action:     ->
      newParams = angular.copy($stateParams)
      newParams.status = undefined
      $state.go 'mc.resource.list', newParams
    }
  ]

  actionsProvider.registerChildAction 'switch-archived-batches', 'switch-archived-batches-archived', ['$state', '$stateParams', ($state, $stateParams) ->
    {
    position:   200
    label:      "Archived"
    icon:       'glyphicon glyphicon-time'
    type:       'warning'
    active:     $stateParams.status == 'archived'
    action:     ->
      newParams = angular.copy($stateParams)
      newParams.status = 'archived'
      $state.go 'mc.resource.list', newParams
    }
  ]



  actionsProvider.registerActionInRole 'run-action', ROLE_ACTION_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.action and $scope.action.state == 'PENDING'

    {
      position: 200
      type:     'success'
      icon:     'glyphicon glyphicon-play'
      label:    'Run'
      action:   ->
        $scope.action.run().then ->
          $scope.reload() if angular.isFunction($scope.reload)
    }
  ]



  actionsProvider.registerActionInRole 'dismiss-action', ROLE_ACTION_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.action and $scope.action.state == 'PENDING'

    {
      position: 500
      type:     'danger'
      icon:     'glyphicon glyphicon-remove'
      label:    'Dismiss'
      action:   ->
        $scope.action.dismiss().then ->
          $scope.reload() if angular.isFunction($scope.reload)
    }
  ]



  actionsProvider.registerActionInRole 'reactivate-action', ROLE_ACTION_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.action and $scope.action.state == 'DISMISSED'

    {
      position: 200
      type:     'success'
      icon:     'glyphicon glyphicon-repeat'
      label:    'Reactivate'
      action:   ->
        $scope.action.reactivate().then ->
          $scope.reload() if angular.isFunction($scope.reload)
    }
  ]

  actionsProvider.registerActionInRole 'repeat-action', ROLE_ACTION_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.action and $scope.action.state == 'FAILED'

    {
      position: 900
      type:     'success'
      icon:     'glyphicon glyphicon-repeat'
      label:    'Retry'
      action:   ->
        $scope.action.reactivate().then ->
          $scope.reload() if angular.isFunction($scope.reload)
    }
  ]


  actionsProvider.registerActionInRoles 'reload-actions', [ROLE_ACTION_ACTION, actionsProvider.ROLE_ITEM_ACTION], ['$scope', ($scope) ->
    return undefined unless angular.isFunction($scope.reload) and ($scope.action and $scope.action.state == 'PERFORMING') or ($scope.batch and not $scope.action)

    {
      position: 900
      type:     'success'
      icon:     'glyphicon glyphicon-refresh'
      label:    'Reload'
      action:   ->
        $scope.reload()
    }
  ]

  actionsProvider.registerActionInRole 'link-actions', ROLE_ACTION_ACTION, ['$scope', '$rootScope', 'messages',($scope, $rootScope, messages) ->
    return undefined unless $scope.action and not ($scope.action.state == 'PERFORMING' or $scope.action.state == 'PERFORMED')

    action = {
      position: 950
      type:     'primary'
      icon:     'glyphicon glyphicon-open'
      label:    'Add or Remove Dependency'
      action:   ->
        if $rootScope.selectedAction == $scope.action
          $rootScope.selectedAction = undefined
        else
          if @mode == 'select'
            $rootScope.selectedAction = $scope.action
          else
            selected = $rootScope.selectedAction
            if @mode == 'add'
              messages.prompt('Add Dependency', 'Please, provide the name of the role for the new dependency').then (role) ->
                selected.addDependency($scope.action.id, role).then ->
                  $scope.reload() if angular.isFunction($scope.reload)
            else if @mode == 'remove'
              messages.confirm('Remove Dependency', 'Do you really want to remove dependency between these two actions? This may cause problems executing given action!').then ->
                selected.removeDependency(selected.dependsOn['' + $scope.action.id]).then ->
                  $scope.reload() if angular.isFunction($scope.reload)
            $rootScope.selectedAction = undefined


    }

    $rootScope.$watch 'selectedAction', (selectedAction) ->
      if selectedAction
        if selectedAction == $scope.action
          action.active = true
          action.icon = 'glyphicon glyphicon-open'
          action.label = 'Add or Remove Dependency'
          action.mode = 'select'
        else
          action.active = false
          if selectedAction.dependsOn.hasOwnProperty('' + $scope.action.id)
            action.icon = 'glyphicon glyphicon-remove-circle'
            action.label = 'Remove Dependency'
            action.mode = 'remove'
          else
            action.icon = 'glyphicon glyphicon-save'
            action.label = 'Select as Dependency'
            action.mode = 'add'

      else
        action.icon = 'glyphicon glyphicon-open'
        action.active = false
        action.label = 'Add or Remove Dependency'
        action.mode = 'select'

    action
  ]


  actionsProvider.registerActionInRole 'run-all-actions-in-batch', actionsProvider.ROLE_ITEM_ACTION, ['$scope', 'messages', 'modelCatalogueApiRoot', 'enhance', 'rest', '$timeout', 'security', ($scope, messages, modelCatalogueApiRoot, enhance, rest, $timeout, security) ->
    return undefined if not security.hasRole('CURATOR')
    return undefined unless $scope.element and angular.isFunction($scope.element.isInstanceOf) and $scope.element.isInstanceOf('batch') or $scope.batch

    action = {
      position: 200
      type:     'success'
      icon:     'glyphicon glyphicon-flash'
      label:    'Run All Pending'
      action:   ->
        batch = $scope.batch ? $scope.element
        messages.confirm('Run All Actions', "Do you really wan to run all actions from '#{batch.name}' batch").then ->
          enhance(rest(method: 'POST', url: "#{modelCatalogueApiRoot}#{batch.link}/run")).then (updated) ->
            updateFrom(batch, updated)
          $timeout($scope.reload, 1000) if angular.isFunction($scope.reload)
    }

    updateDisabled = (batch) ->
      return unless batch
      action.disabled = not batch.pending.total

    updateDisabled($scope.batch ? $scope.element)

    $scope.$watch 'batch', updateDisabled
    $scope.$watch 'element', updateDisabled

    action

  ]



  actionsProvider.registerActionInRole 'update-action-parameters', ROLE_ACTION_ACTION, ['$scope', 'messages', 'names', 'security', ($scope, messages, names, security) ->
    return undefined if not $scope.action
    return undefined if $scope.action.state in ['PERFORMING', 'PERFORMED']
    return undefined if not security.hasRole('CURATOR')

    action =
      position:   100
      label:      'Update Action Parameters'
      icon:       'glyphicon glyphicon-edit'
      type:       'primary'
      action:     ->
        messages.prompt('Update Action Parameters', '', {type: 'update-action-parameters', action: $scope.action}).then (updated)->
          $scope.action = updated

    updateAction = ->
      action.disabled = $scope.action.state in ['PERFORMING', 'PERFORMED']

    $scope.$watch 'action.state', updateAction

    updateAction()

    return action

  ]

  actionsProvider.registerActionInRole 'modal-cancel', actionsProvider.ROLE_MODAL_ACTION, ['$scope', ($scope) ->
    return undefined if not $scope.$dismiss

    {
      position:   10000
      label:      'Cancel'
      icon:       'glyphicon glyphicon-ban-circle'
      type:       'warning'
      action: -> $scope.$dismiss()
    }
  ]

  actionsProvider.registerActionInRole 'modal-save-element', actionsProvider.ROLE_MODAL_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.hasChanged and $scope.saveElement

    action = {
      position:   1000
      label:      'Save'
      icon:       'glyphicon glyphicon-ok'
      type:       'success'
      action: ->
       $scope.saveElement() if $scope.hasChanged()
    }

    $scope.$watch 'hasChanged()', (changed)->
      action.disabled = not changed

    action
  ]

  actionsProvider.registerActionInRole 'modal-save-and-add-another', actionsProvider.ROLE_MODAL_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.hasChanged and $scope.saveAndCreateAnother

    action = {
      position:   2000
      label:      'Save and Create Another'
      icon:       'glyphicon glyphicon-ok'
      type:       'success'
      action: ->
        $scope.saveAndCreateAnother() if $scope.hasChanged()
    }

    $scope.$watch 'hasChanged()', (changed)->
      action.disabled = not changed

    action
  ]


  actionsProvider.registerChildAction 'modal-save-element', 'modal-save-element-as-new-version', ['$scope', ($scope) ->
    return undefined unless $scope.hasChanged and $scope.saveElement and not $scope.create and $scope.original and $scope.original.isInstanceOf and $scope.original.isInstanceOf 'catalogueElement'

    action = {
      position:   1000
      label:      'Save as New Version'
      icon:       'glyphicon glyphicon-circle-arrow-up'
      type:       'success'
      action: ->
        $scope.saveElement(true) if $scope.hasChanged()
    }

    $scope.$watch 'hasChanged()', (changed)->
      action.disabled = not changed

    action
  ]

  actionsProvider.registerActionInRole 'expand-all-rows', actionsProvider.ROLE_LIST_HEADER_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.rows

    {
      position:   -10000
      label:      'Expand All'
      icon:       'fa fa-plus-square-o'
      type:       'primary'
      active:     false
      action: ->
        $scope.$$expandAll = not @active

        @active = not @active
        if @active
          @label = "Collapse All"
          @icon  = 'fa fa-minus-square-o'
        else
          @label = 'Expand All'
          @icon  = 'fa fa-plus-square-o'

    }
  ]

]