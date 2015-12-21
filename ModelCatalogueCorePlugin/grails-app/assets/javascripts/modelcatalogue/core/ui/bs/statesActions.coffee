angular.module('mc.core.ui.bs.statesActions', ['mc.util.ui.actions']).config ['actionsProvider', 'names', (actionsProvider, names)->

  showErrorsUsingMessages = (messages) ->
    (response) ->
      if response?.data and response.data.errors
        if angular.isString response.data.errors
          messages.error response.data.errors
        else
          for err in response.data.errors
            messages.error err.message


  actionsProvider.registerActionInRoles 'change-element-state', [actionsProvider.ROLE_ITEM_ACTION], ['$scope', 'security', 'names', ($scope, security, names)->
    return undefined unless security.hasRole('CURATOR')
    return undefined unless $scope.element
    return undefined unless $scope.element.status
    return undefined unless angular.isFunction $scope.element.isInstanceOf
    return undefined unless $scope.element.isInstanceOf('dataModel')

    action = {
      position:   10000
      label:      'Status'
      icon:       'fa fa-check-circle'
      type:       'primary'
      watches:    'element.status'
      expandToLeft: true
    }

    updateStatus = (status) ->
      action.icon = 'fa fa-pencil'          if status == 'DRAFT'
      action.icon = 'fa fa-clock-o'         if status == 'PENDING'
      action.icon = 'fa fa-check-circle'    if status == 'FINALIZED'
      action.icon = 'fa fa-ban'             if status == 'DEPRECATED'

      action.label = names.getNaturalName(status?.toLowerCase() ? 'status')

    updateStatus $scope.element.status

    action
  ]

  actionsProvider.registerChildActionInRole 'change-element-state', 'create-new-version', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'security', ($rootScope, $scope, messages, security) ->
    return undefined if not $scope.element
    return undefined if not $scope.element.status
    return undefined if not security.hasRole('CURATOR')



    {
      position:   100
      label:      'New Version'
      icon:       'fa fa-fw fa-arrow-circle-up'
      type:       'primary'
      watches:    ['element.status', 'element.archived']
      disabled:   $scope.element.archived || $scope.element.status == 'DRAFT'
      action:     ->
        messages.prompt(null, null, type: 'new-version', element: $scope.element)
    }
  ]

  actionsProvider.registerChildActionInRole 'change-element-state', 'finalize', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'security', ($rootScope, $scope, messages, security) ->
    return undefined if not $scope.element
    return undefined if not $scope.element.status
    return undefined if not security.hasRole('CURATOR')

    {
      position:   200
      label:      'Finalize'
      icon:       'fa fa-fw fa-check-circle'
      type:       'primary'
      disabled:   $scope.element?.status != 'DRAFT'
      watches:    ['element.status', 'element.archived']
      action:     ->
        messages.prompt(null, null, type: 'finalize', element: $scope.element)
    }
  ]

  actionsProvider.registerChildActionInRole 'change-element-state', 'archive', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', 'enhance', 'rest', 'modelCatalogueApiRoot', ($rootScope, $scope, messages, names, security, enhance, rest, modelCatalogueApiRoot) ->
    return undefined if not $scope.element
    return undefined if not $scope.element.status
    return undefined if not security.hasRole('CURATOR')

    action = {
      position:   400
      watches:    ['element.status', 'element.archived']
      action:     ->
        if $scope.element.archived
          if security.hasRole('ADMIN')
            messages.confirm("Do you want to restore #{$scope.element.getElementTypeName()} #{$scope.element.name} as finalized?", "The #{$scope.element.getElementTypeName()} #{$scope.element.name} will no longer be deprecated").then ->
              enhance(rest(url: "#{modelCatalogueApiRoot}#{$scope.element.link}/restore", method: 'POST')).then (restored) ->
                $scope.element.updateFrom restored
                $rootScope.$broadcast 'catalogueElementUpdated', restored
                $rootScope.$broadcast 'redrawContextualActions'
              , showErrorsUsingMessages(messages)
        else
          messages.confirm("Do you want to mark #{$scope.element.getElementTypeName()} #{$scope.element.name} as deprecated?", "The #{$scope.element.getElementTypeName()} #{$scope.element.name} will be marked as deprecated").then ->
            enhance(rest(url: "#{modelCatalogueApiRoot}#{$scope.element.link}/archive", method: 'POST')).then (archived) ->
              $scope.element.updateFrom archived
              $rootScope.$broadcast 'catalogueElementUpdated', archived
              $rootScope.$broadcast 'redrawContextualActions'
            , showErrorsUsingMessages(messages)
    }

    if $scope.element.archived
      if not security.hasRole('ADMIN')
        action.disabled = true
      else
        action.label = 'Restore'
        action.icon = 'fa fa-fw fa-repeat'
        action.type = 'primary'
    else
      action.label = 'Mark as Deprecated'
      action.icon = 'fa fa-fw fa-ban'
      action.type = 'danger'

    action
  ]

  actionsProvider.registerChildActionInRole 'change-element-state', 'merge', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', 'enhance', 'rest', 'modelCatalogueApiRoot', ($rootScope, $scope, messages, names, security, enhance, rest, modelCatalogueApiRoot) ->
    return undefined if not $scope.element
    return undefined if not $scope.element.status
    return undefined if not security.hasRole('CURATOR')

    {
      position:   300
      label:      'Merge'
      icon:       'fa fa-fw fa-code-fork fa-rotate-180 fa-flip-vertical'
      type:       'danger'
      watches:    ['element.status', 'element.archived']
      disabled:   $scope.element.status != 'DRAFT'
      action:     ->
        messages.prompt("Merge #{$scope.element.getElementTypeName()} #{$scope.element.name} to another #{$scope.element.getElementTypeName()}", "All non-system relationships of the #{$scope.element.getElementTypeName()} #{$scope.element.name} will be moved to the following destination and than the #{$scope.element.getElementTypeName()} #{$scope.element.name} will be archived", {type: 'catalogue-element', resource: $scope.element.elementType, status: 'draft'}).then (destination)->
          enhance(rest(url: "#{modelCatalogueApiRoot}#{$scope.element.link}/merge/#{destination.id}", method: 'POST')).then (merged) ->
            oldName = $scope.element.classifiedName
            messages.success "Element #{oldName} merged successfully into  #{$scope.element.classifiedName}"
            merged.show()
            $rootScope.$broadcast 'redrawContextualActions'
          , showErrorsUsingMessages(messages)
    }
  ]


#  actionsProvider.registerChildActionInRole 'change-element-state', 'delete', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', '$state', 'messages', 'names', 'security', ($rootScope, $scope, $state, messages, names, security) ->
#    return undefined if not $scope.element
#    return undefined if not angular.isFunction($scope.element.delete)
#    return undefined if not security.hasRole('ADMIN')
#
#    {
#      position:   500
#      label:      'Delete'
#      icon:       'fa fa-fw fa-times-circle'
#      type:       'danger'
#      action:     ->
#        messages.confirm("Do you really want to delete #{$scope.element.getElementTypeName()} #{$scope.element.name} ?", "The #{$scope.element.getElementTypeName()} #{$scope.element.name} will be deleted permanently. This action cannot be undone.").then ->
#          $scope.element.delete()
#          .then ->
#            messages.success "#{$scope.element.getElementTypeName()} #{$scope.element.name} deleted."
#            if $state.current.name.indexOf('mc.resource.show') >= 0
#              $state.go('mc.resource.list', {resource: names.getPropertyNameFromType($scope.element.elementType)}, {reload: true})
#          .catch showErrorsUsingMessages(messages)
#    }
#  ]
]