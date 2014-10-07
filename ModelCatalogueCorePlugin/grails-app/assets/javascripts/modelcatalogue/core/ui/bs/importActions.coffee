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

]