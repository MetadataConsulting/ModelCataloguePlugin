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

  actionsProvider.registerAction 'edit-catalogue-element', ['$scope', 'messages', 'names', 'security', ($scope, messages, names, security) ->
    return undefined if not $scope.element
    return undefined if not security.hasRole('CURATOR')

    {
      position:   100
      label:      'Edit'
      icon:       'edit'
      type:       'primary'
      disabled:   $scope.element.archived or $scope.element?.status == 'FINALIZED'
      action:     ->
        messages.prompt('Edit ' + $scope.element.elementTypeName, '', {type: 'edit-' + names.getPropertyNameFromType($scope.element.elementType), element: $scope.element}).then (updated)->
          $scope.element = updated
    }
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

  actionsProvider.registerAction 'export', ['$scope', ($scope)->
    return undefined unless $scope.list or $scope.element

    {
      position:   1000
      label:      'Export'
      icon:       'download-alt'
      type:       'primary'
    }
  ]

  actionsProvider.registerChildAction 'export', 'catalogue-element-export-specific-reports' , ['$scope', '$window', ($scope, $window) ->
    return undefined if not $scope.element

    {
      position:   1000
      label:      "#{$scope.element.name} Reports"
      disabled:   not $scope.element?.availableReports?.length
      generator:  (action) ->
        action.createActionsFrom 'element.availableReports', (reports = []) ->
          for report in reports
            {
              label:  report.title
              action: -> $window.open(report.url, '_blank') ; return true
            }
    }
  ]

  actionsProvider.registerChildAction 'export', 'generic-reports', ['$scope', '$window', ($scope, $window) ->
    {
      position:   2000
      label:      "Other Reports"
      disabled:   not $scope.reports?.length
      generator: (action) ->
        action.createActionsFrom 'reports', (reports = []) ->
          for report in reports
            {
              label:  report.title
              action: -> $window.open(report.url, '_blank') ; return true
            }
    }
  ]

  actionsProvider.registerChildAction 'export', 'list-exports-current', ['$scope', '$window', ($scope, $window) ->
    return undefined if not $scope.list?

    {
      position:   5000
      label:      "Current Reports"
      disabled:   not $scope.list.availableReports?.length
      generator:  (action) ->
        action.createActionsFrom 'list.availableReports', (reports = []) ->
          for report in reports
            {
              label:  report.title
              action: -> $window.open(report.url, '_blank') ; return true
            }
    }
  ]

  actionsProvider.registerAction 'switch-status', ['$state', '$scope', '$stateParams', ($state, $scope, $stateParams) ->
    return undefined unless $state.current.name == 'mc.resource.list' and $scope.list and not $scope.noStatusSwitch

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

]