angular.module('mc.core.ui.bs.importActions', ['mc.util.ui.actions']).config ['actionsProvider', (actionsProvider)->

  actionsProvider.registerActionInRole 'resolveAll', actionsProvider.ROLE_ITEM_ACTION, ['$scope', '$rootScope', 'modelCatalogueDataArchitect', 'security', ($scope, $rootScope, modelCatalogueDataArchitect, security)->
    return undefined unless $scope.element
    return undefined if not angular.isFunction $scope.element.isInstanceOf
    return undefined unless $scope.element.isInstanceOf 'dataImport'
    return undefined if not security.hasRole('CURATOR')
    action = {
    position:   1000
    label:      'Resolve All'
    icon:       'glyphicon glyphicon-thumbs-up'
    type:       'primary'
    action:     ->
      modelCatalogueDataArchitect.resolveAll($scope.element.id).then ->
        $rootScope.$broadcast 'actionsResolved', $scope.element
    }

    $scope.$watch 'element.pendingAction.total', (newTotal) ->
      action.disabled = newTotal == 0

    return action
  ]

  actionsProvider.registerActionInRole 'ingestQueue', actionsProvider.ROLE_ITEM_ACTION, ['$scope', '$rootScope', 'modelCatalogueDataArchitect', 'security', ($scope, $rootScope, modelCatalogueDataArchitect, security)->
    return undefined unless $scope.element
    return undefined if not angular.isFunction $scope.element.isInstanceOf
    return undefined unless $scope.element.isInstanceOf 'dataImport'
    return undefined if not security.hasRole('CURATOR')
    action = {
      position:   1000
      label:      'Ingest Queue'
      icon:       'glyphicon glyphicon-ok-circle'
      type:       'primary'
      action:     ->
        modelCatalogueDataArchitect.ingestQueue($scope.element.id).then ->
          $rootScope.$broadcast 'queueIngested', $scope.element
    }

    $scope.$watch 'element.importQueue.total', (newTotal) ->
      action.disabled = newTotal == 0

    return action
  ]

  actionsProvider.registerActionInRole 'resolveRow', actionsProvider.ROLE_ITEM_ACTION, ['$scope', '$rootScope', 'modelCatalogueDataArchitect', 'security', 'messages', ($scope, $rootScope, modelCatalogueDataArchitect, security, messages)->
    return undefined unless $scope.element
    return undefined if not angular.isFunction $scope.element.isInstanceOf
    return undefined unless $scope.element.isInstanceOf 'importRow'
    return undefined if not security.hasRole('CURATOR')

    action = {
      label:  'Resolve'
      icon:   'glyphicon glyphicon-thumbs-up'
      type:   'default'
      disabled: $scope.element.imported

      action: ->
        rel = $scope.element

        deferred = $q.defer()
        messages.confirm('Resolve Actions', "Do you really want to resolve all actions : \n\n#{rel.actions.join('\n\n')}?").then () ->
          rel.action().then ->
            messages.success('Row actions resolved!', "actions are resolved")
            # reloads the table
            deferred.resolve(true)
          , (response) ->
            if response.status == 404
              messages.error('Error resolving actions', 'Actions cannot be resolve, it probably does not exist anymore. The table was refreshed to get the most up to date results.')
              deferred.resolve(true)
            else
              messages.error('Error on action', 'Actions cannot be resolved. Possibly there is an error that needs user input')

        deferred.promise

    }


    $scope.$watch 'element.imported', (imported) ->
      action.disabled = imported

    action
  ]

]