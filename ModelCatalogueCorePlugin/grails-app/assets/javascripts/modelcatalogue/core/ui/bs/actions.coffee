angular.module('mc.core.ui.bs.actions', ['mc.util.ui.actions']).config ['actionsProvider', (actionsProvider)->

  actionsProvider.registerAction 'create-catalogue-element', ['$scope', 'names', 'security', 'messages', ($scope, names, security, messages) ->
    return undefined if not security.hasRole('CURATOR')
    return undefined if not $scope.resource
    return undefined if not messages.hasPromptFactory('edit-' + $scope.resource)

    {
    position:   100
    label:      "New #{names.getNaturalName($scope.resource)}"
    icon:       'plus-sign'
    type:       'success'
    action:     ->
      messages.prompt('Create ' + names.getNaturalName($scope.resource), '', {type: 'edit-' + $scope.resource, create: ($scope.resource)}).then (created)->
        created.show()
    }
  ]

  actionsProvider.registerChildAction 'create-catalogue-element', 'create-enumerated-type', ['security', '$scope', 'messages', (security, $scope, messages)->
    return undefined if not security.hasRole('CURATOR')
    return undefined if not messages.hasPromptFactory('edit-enumeratedType')
    return undefined if $scope.resource != 'dataType'

    {
    position:   100
    label:      "New Enumerated Type"
    #icon:      'plus-sign'
    type:       'success'
    action:     ->
      messages.prompt('Create Enumerated Type', '', {type: 'edit-enumeratedType', create: ($scope.resource)}).then (created)->
        created.show()
    }
  ]

  actionsProvider.registerAction 'create-import', ['security', '$scope', 'messages', (security, $scope, messages)->
    return undefined unless security.hasRole('CURATOR') and $scope.resource == 'import' and messages.hasPromptFactory('new-import')
    {
    position:   100
    label:      "New Import"
    icon:       'plus-sign'
    type:       'success'
    action:     ->
      messages.prompt('Create Import', '', {type: 'new-import', create: ($scope.resource)}).then (created)->
        created.show()
    }
  ]

  actionsProvider.registerAction 'resolveAll', ['$scope', '$rootScope', 'modelCatalogueDataArchitect', 'security', ($scope, $rootScope, modelCatalogueDataArchitect, security)->
    return undefined unless $scope.element
    return undefined unless $scope.element.elementTypeName == 'Data Import'
    return undefined if not security.hasRole('CURATOR')
    action = {
    position:   1000
    label:      'Resolve All'
    icon:       'thumbs-up'
    type:       'primary'
    action:     ->
      modelCatalogueDataArchitect.resolveAll($scope.element.id).then ->
        $rootScope.$broadcast 'actionsResolved', $scope.element
    }

    $scope.$watch 'element.pendingAction.total', (newTotal) ->
      action.disabled = newTotal == 0

    return action
  ]

  actionsProvider.registerAction 'ingestQueue', ['$scope', '$rootScope', 'modelCatalogueDataArchitect', 'security', ($scope, $rootScope, modelCatalogueDataArchitect, security)->
    return undefined unless $scope.element
    return undefined unless $scope.element.elementTypeName == 'Data Import'
    return undefined if not security.hasRole('CURATOR')
    action = {
      position:   1000
      label:      'Ingest Queue'
      icon:       'ok-circle'
      type:       'primary'
      action:     ->
        modelCatalogueDataArchitect.ingestQueue($scope.element.id).then ->
          $rootScope.$broadcast 'queueIngested', $scope.element
    }

    $scope.$watch 'element.importQueue.total', (newTotal) ->
      action.disabled = newTotal == 0

    return action
  ]


  actionsProvider.registerAction 'edit-catalogue-element', ['$scope', 'messages', 'names', 'security', ($scope, messages, names, security) ->
    return undefined if not $scope.element
    return undefined if $scope.element.elementTypeName == 'Data Import'
    return undefined if not security.hasRole('CURATOR')

    action =
      position:   100
      label:      'Edit'
      icon:       'edit'
      type:       'primary'
      disabled:   $scope.element.archived or $scope.element?.status == 'FINALIZED'
      action:     ->
        messages.prompt('Edit ' + $scope.element.elementTypeName, '', {type: 'edit-' + names.getPropertyNameFromType($scope.element.elementType), element: $scope.element}).then (updated)->
          $scope.element = updated

    updateAction = ->
      action.disabled = $scope.element.archived or $scope.element?.status == 'FINALIZED'

    $scope.$watch 'element.status', updateAction
    $scope.$watch 'element.archived', updateAction

    return action

  ]


  actionsProvider.registerAction 'create-new-version', ['$scope', 'messages', 'names', 'security', 'catalogueElementResource', ($scope, messages, names, security, catalogueElementResource) ->
    return undefined if not $scope.element
    return undefined if not $scope.element.status
    return undefined if not security.hasRole('CURATOR')

    {
    position:   150
    label:      'New Version'
    icon:       'circle-arrow-up'
    type:       'primary'
    action:     ->
      messages.confirm('Do you want to create new version?', "New version will be created for #{$scope.element.elementTypeName} #{$scope.element.name}").then ->
        catalogueElementResource($scope.element.elementType).update($scope.element, {newVersion: true}).then (updated) ->
          $scope.element = updated
          messages.success("New version created for #{$scope.element.name}")
    }
  ]

  actionsProvider.registerAction 'create-new-relationship', ['$scope', 'messages', 'names', 'security', ($scope, messages, names, security) ->
    return undefined if not $scope.element
    return undefined if not $scope.element.isInstanceOf('org.modelcatalogue.core.CatalogueElement')
    return undefined if $scope.element.elementTypeName == 'Data Import'
    return undefined if not security.hasRole('CURATOR')

    {
    position:   200
    label:      'Create Relationship'
    icon:       'link'
    type:       'success'
    action:     ->
      messages.prompt('Create Relationship', '', {type: 'new-relationship', element: $scope.element}).then (updated)->
        $scope.element = updated
    }
  ]

  actionsProvider.registerAction 'download-asset', [ '$scope', '$window', ($scope, $window) ->
    return undefined if not $scope.element?.downloadUrl?

    {
      position:   0
      label:      'Download'
      icon:       'download'
      type:       'primary'
      action:     ->
        $window.open $scope.element.downloadUrl, '_blank'; return true

    }
  ]

  actionsProvider.registerAction 'refresh-asset', [ '$scope', '$rootScope', 'catalogueElementResource', ($scope, $rootScope, catalogueElementResource) ->
    return undefined if $scope.element?.elementType != 'org.modelcatalogue.core.Asset'
    return undefined if $scope.element.status != 'PENDING'

    {
      position:   0
      label:      'Refresh'
      icon:       'refresh'
      type:       'primary'
      action:     ->
        catalogueElementResource($scope.element.elementType).get($scope.element.id).then (refreshed) ->
          $scope.element = refreshed
          $rootScope.$broadcast 'redrawContextualActions'

    }
  ]

  actionsProvider.registerAction 'export', ['$scope', 'security', ($scope, security)->
    return undefined unless security.isUserLoggedIn()
    return undefined unless $scope.list or $scope.element
    if $scope.list
      return undefined if $scope.resource == 'import'
    if $scope.element
      return undefined if $scope.element.elementTypeName == 'Data Import'
    {
    position:   1000
    label:      'Export'
    icon:       'download-alt'
    type:       'primary'
    }
  ]

  generateReports = ($scope, $window, enhance, rest) ->
    (reports = []) ->
      for report in reports
        {
          label:  report.title
          url:    report.url
          action: ->
            switch report.type
              when 'LINK' then $window.open(@url, '_blank')
              else enhance(rest(method: 'GET', url: @url)).then (result) ->
                result.show()
            return true
        }

  actionsProvider.registerChildAction 'export', 'catalogue-element-export-specific-reports' , ['$scope', '$window', 'enhance', 'rest', ($scope, $window, enhance, rest) ->
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

  actionsProvider.registerChildAction 'export', 'list-exports-current', ['$scope', '$window', 'enhance', 'rest', ($scope, $window, enhance, rest) ->
    return undefined if not $scope.list?

    {
    position:   5000
    label:      "Current Reports"
    disabled:   not $scope.list.availableReports?.length
    generator:  (action) ->
      action.createActionsFrom 'list.availableReports', generateReports($scope, $window, enhance, rest)
    }
  ]

  actionsProvider.registerAction 'switch-status', ['$state', '$scope', '$stateParams', ($state, $scope, $stateParams) ->
    return undefined unless $state.current.name == 'mc.resource.list' and $scope.list and not $scope.noStatusSwitch and $stateParams.resource in ['model', 'dataElement', 'asset']

    {
    abstract: true
    position: 500

    type:     (->
      return 'info'     if $stateParams.status == 'draft'
      return 'warning'  if $stateParams.status == 'pending'
      return 'primary'
    )()
    icon:     (->
      return 'pencil'   if $stateParams.status == 'draft'
      return 'time'     if $stateParams.status == 'pending'
      return 'ok'
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
    icon:       'ok'
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
      icon:       'time'
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
    icon:       'pencil'
    type:       'info'
    active:     $stateParams.status == 'draft'
    action:     ->
      newParams = angular.copy($stateParams)
      newParams.status = 'draft'
      $state.go 'mc.resource.list', newParams
    }
  ]


  actionsProvider.registerAction 'switch-archived-batches', ['$state', '$scope', '$stateParams', ($state, $scope, $stateParams) ->
    return undefined unless $state.current.name == 'mc.resource.list' and $scope.list and $stateParams.resource == 'batch'

    {
    abstract: true
    position: 500

    type:     (->
      return 'info'     if $stateParams.status == 'archived'
      return 'primary'
    )()
    icon:     (->
      return 'time'     if $stateParams.status == 'archived'
      return 'ok'
    )()
    label:    (->
      return 'Archived'  if $stateParams.status == 'archived'
      return 'Active'
    )()
    }
  ]

  actionsProvider.registerChildAction 'switch-archived-batches', 'switch-archived-batches-active', ['$state', '$stateParams', ($state, $stateParams) ->
    {
    position:   300
    label:      "Active"
    icon:       'ok'
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
    icon:       'time'
    type:       'warning'
    active:     $stateParams.status == 'archived'
    action:     ->
      newParams = angular.copy($stateParams)
      newParams.status = 'archived'
      $state.go 'mc.resource.list', newParams
    }
  ]



  actionsProvider.registerAction 'run-action', ['$scope', ($scope) ->
    return undefined unless $scope.action and $scope.action.state == 'PENDING'

    {
      position: 100
      type:     'success'
      icon:     'play'
      label:    'Run'
      action:   ->
        $scope.action.run().then ->
          $scope.reload() if angular.isFunction($scope.reload)
    }
  ]



  actionsProvider.registerAction 'dismiss-action', ['$scope', ($scope) ->
    return undefined unless $scope.action and $scope.action.state == 'PENDING'

    {
      position: 500
      type:     'danger'
      icon:     'remove'
      label:    'Dismiss'
      action:   ->
        $scope.action.dismiss().then ->
          $scope.reload() if angular.isFunction($scope.reload)
    }
  ]



  actionsProvider.registerAction 'reactivate-action', ['$scope', ($scope) ->
    return undefined unless $scope.action and $scope.action.state == 'DISMISSED'

    {
      position: 100
      type:     'success'
      icon:     'repeat'
      label:    'Reactivate'
      action:   ->
        $scope.action.reactivate().then ->
          $scope.reload() if angular.isFunction($scope.reload)
    }
  ]

  actionsProvider.registerAction 'repeat-action', ['$scope', ($scope) ->
    return undefined unless $scope.action and $scope.action.state == 'FAILED'

    {
      position: 900
      type:     'success'
      icon:     'repeat'
      label:    'Retry'
      action:   ->
        $scope.action.reactivate().then ->
          $scope.reload() if angular.isFunction($scope.reload)
    }
  ]


  actionsProvider.registerAction 'reload-actions', ['$scope', ($scope) ->
    return undefined unless angular.isFunction($scope.reload) and ($scope.action and $scope.action.state == 'PERFORMING') or ($scope.batch and not $scope.action)

    {
      position: 900
      type:     'success'
      icon:     'refresh'
      label:    'Reload'
      action:   ->
        $scope.reload()
    }
  ]


  actionsProvider.registerAction 'run-all-actions-in-batch', ['$scope', '$q', ($scope, $q) ->
    return undefined unless $scope.pendingActions and not $scope.action

    runAllAction = {
      position: 100
      type:     'success'
      icon:     'play'
      label:    'Run All Pending'
      action:   ->
        promises = []
        for action in $scope.pendingActions
          promises.push action.run() if action.state == 'PENDING'
        $q.all(promises).then ->
          $scope.reload() if angular.isFunction($scope.reload)
    }

    $scope.$watch 'pendingActions', (pendingActions) ->
      runAllAction.disabled = pendingActions.length == 0

    runAllAction
  ]



]