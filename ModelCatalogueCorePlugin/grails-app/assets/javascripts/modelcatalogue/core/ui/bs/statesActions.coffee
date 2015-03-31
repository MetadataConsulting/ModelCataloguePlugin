angular.module('mc.core.ui.bs.statesActions', ['mc.util.ui.actions']).config ['actionsProvider', 'names', (actionsProvider, names)->

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


  actionsProvider.registerActionInRoles 'change-element-state', [actionsProvider.ROLE_ITEM_ACTION], ['$scope', 'security', 'names', ($scope, security, names)->
    return undefined if not security.hasRole('CURATOR')
    return undefined unless $scope.element
    return undefined unless $scope.element.status
    if $scope.element
      return undefined if not angular.isFunction $scope.element.isInstanceOf
      return undefined if $scope.element.isInstanceOf 'dataImport'

    action = {
      position:   2000
      label:      'Status'
      icon:       'fa fa-check-circle'
      type:       'primary'
      expandToLeft: true
    }

    $scope.$watch 'element.status', (status) ->
      action.icon = 'fa fa-pencil'          if status == 'DRAFT'
      action.icon = 'fa fa-clock-o'         if status == 'PENDING'
      action.icon = 'fa fa-check-circle'    if status == 'FINALIZED'
      action.icon = 'fa fa-ban'             if status == 'DEPRECATED'

      action.label = names.getNaturalName(status?.toLowerCase() ? 'status')

    action
  ]

  actionsProvider.registerChildActionInRole 'change-element-state', 'create-new-version', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', 'catalogueElementResource', ($rootScope, $scope, messages, names, security, catalogueElementResource) ->
    return undefined if not $scope.element
    return undefined if not $scope.element.status
    return undefined if not security.hasRole('CURATOR')

    action = {
      position:   100
      label:      'New Version'
      icon:       'fa fa-fw fa-arrow-circle-up'
      type:       'primary'
      action:     ->
        messages.confirm('Do you want to create new version?', "New version will be created for #{$scope.element.getElementTypeName()} #{$scope.element.name}").then ->
          catalogueElementResource($scope.element.elementType).update($scope.element, {newVersion: true}).then (updated) ->
            updateFrom $scope.element, updated
            messages.success("New version created for #{$scope.element.name}")
            $rootScope.$broadcast 'newVersionCreated', updated
          , showErrorsUsingMessages(messages)
    }

    updateAction = ->
      action.disabled = $scope.element.archived || $scope.element.status == 'DRAFT'

    $scope.$watch 'element.status', updateAction
    $scope.$watch 'element.archived', updateAction
    $scope.$on 'newVersionCreated', updateAction

    action
  ]

  actionsProvider.registerChildActionInRole 'change-element-state', 'finalize', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', 'catalogueElementResource', 'enhance', 'rest', 'modelCatalogueApiRoot', ($rootScope, $scope, messages, names, security, catalogueElementResource, enhance, rest, modelCatalogueApiRoot) ->
    return undefined if not $scope.element
    return undefined if not $scope.element.status
    return undefined if not security.hasRole('CURATOR')

    action = {
      position:   200
      label:      'Finalize'
      icon:       'fa fa-fw fa-check-circle'
      type:       'primary'
      action:     ->
        messages.confirm("Do you want to finalize #{$scope.element.getElementTypeName()} #{$scope.element.name} ?", "The #{$scope.element.getElementTypeName()} #{$scope.element.name} and all it's dependencies will be finalized recursively. This means all elements classfied by classifications, all child models and data elements of models, all value domains of data elements and all data types of value domains. If any value domain is using measurement unit which is not finalized yet the whole finalization process will fail.").then ->
          enhance(rest(url: "#{modelCatalogueApiRoot}#{$scope.element.link}/finalize", method: 'POST')).then (finalized) ->
            updateFrom $scope.element, finalized
            $rootScope.$broadcast 'catalogueElementUpdated', finalized
            $rootScope.$broadcast 'catalogueElementFinalized', finalized
          , showErrorsUsingMessages(messages)
    }

    updateAction = ->
      action.disabled = $scope.element?.status != 'DRAFT'

    $scope.$watch 'element.status', updateAction
    $scope.$watch 'element.archived', updateAction
    $scope.$on 'newVersionCreated', updateAction

    action
  ]

  actionsProvider.registerChildActionInRole 'change-element-state', 'archive', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', 'enhance', 'rest', 'modelCatalogueApiRoot', ($rootScope, $scope, messages, names, security, enhance, rest, modelCatalogueApiRoot) ->
    return undefined if not $scope.element
    return undefined if not $scope.element.status
    return undefined if not security.hasRole('CURATOR')

    action = {
      position:   400
      label:      'Mark as Deprecated'
      icon:       'fa fa-fw fa-ban'
      type:       'danger'
      action:     ->
        messages.confirm("Do you want to mark #{$scope.element.getElementTypeName()} #{$scope.element.name} as deprecated?", "The #{$scope.element.getElementTypeName()} #{$scope.element.name} will be marked as deprecated").then ->
          enhance(rest(url: "#{modelCatalogueApiRoot}#{$scope.element.link}/archive", method: 'POST')).then (archived) ->
            updateFrom $scope.element, archived
            $rootScope.$broadcast 'catalogueElementUpdated', archived
          , showErrorsUsingMessages(messages)
    }

    updateAction = ->
      action.disabled = $scope.element.archived

    $scope.$watch 'element.status', updateAction
    $scope.$watch 'element.archived', updateAction
    $scope.$on 'newVersionCreated', updateAction

    action
  ]

  actionsProvider.registerChildActionInRole 'change-element-state', 'merge', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', 'enhance', 'rest', 'modelCatalogueApiRoot', ($rootScope, $scope, messages, names, security, enhance, rest, modelCatalogueApiRoot) ->
    return undefined if not $scope.element
    return undefined if not $scope.element.status
    return undefined if not security.hasRole('CURATOR')

    action = {
      position:   300
      label:      'Merge'
      icon:       'fa fa-fw fa-code-fork fa-rotate-180 fa-flip-vertical'
      type:       'danger'
      action:     ->
        messages.prompt("Merge #{$scope.element.getElementTypeName()} #{$scope.element.name} to another #{$scope.element.getElementTypeName()}", "All non-system relationships of the #{$scope.element.getElementTypeName()} #{$scope.element.name} will be moved to the following destination and than the #{$scope.element.getElementTypeName()} #{$scope.element.name} will be archived", {type: 'catalogue-element', resource: $scope.element.elementType, status: 'draft'}).then (destination)->
          enhance(rest(url: "#{modelCatalogueApiRoot}#{$scope.element.link}/merge/#{destination.id}", method: 'POST')).then (merged) ->
            oldName = $scope.element.classifiedName
            messages.success "Element #{oldName} merged successfully into  #{$scope.element.classifiedName}"
            merged.show()
          , showErrorsUsingMessages(messages)
    }

    updateAction = ->
      action.disabled = $scope.element.status != 'DRAFT'

    $scope.$watch 'element.status', updateAction
    $scope.$watch 'element.archived', updateAction
    $scope.$on 'newVersionCreated', updateAction

    action
  ]


  actionsProvider.registerChildActionInRole 'change-element-state', 'delete', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', '$state', 'messages', 'names', 'security', ($rootScope, $scope, $state, messages, names, security) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction($scope.element.delete)
    return undefined if not security.hasRole('ADMIN')

    action = {
      position:   500
      label:      'Delete'
      icon:       'fa fa-fw fa-times-circle'
      type:       'danger'
      action:     ->
        messages.confirm("Do you really want to delete #{$scope.element.getElementTypeName()} #{$scope.element.name} ?", "The #{$scope.element.getElementTypeName()} #{$scope.element.name} will be deleted permanently. This action cannot be undone.").then ->
          $scope.element.delete()
          .then ->
            messages.success "#{$scope.element.getElementTypeName()} #{$scope.element.name} deleted."
            if $state.current.name.indexOf('mc.resource.show') >= 0
              $state.go('mc.resource.list', {resource: names.getPropertyNameFromType($scope.element.elementType)}, {reload: true})
          .catch showErrorsUsingMessages(messages)
    }

    action
  ]
]