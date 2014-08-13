angular.module('mc.core.ui.batchView', ['mc.core.catalogueElementEnhancer', 'mc.core.listReferenceEnhancer', 'mc.core.listEnhancer', 'mc.util.names', 'mc.util.messages', 'mc.core.ui.columns', 'mc.util.ui.actions', 'ui.router', 'mc.core.ui.catalogueElementProperties']).directive 'batchView',  [-> {
    restrict: 'E'
    replace: true
    scope:
      batch: '='
      property: '=?'
      id: '@'

    templateUrl: 'modelcatalogue/core/ui/batchView.html'

    controller: ['$scope','$q', ($scope, $q) ->
      $scope.getType = (action) ->
        return 'info'    if not action
        return 'warning' if action.state == 'PERFORMING'
        return 'warning' if action.state == 'DISMISSED'
        return 'success' if action.state == 'PERFORMED'
        return 'danger'  if action.state == 'FAILED'
        return 'info'

      $scope.pendingActions = []
      $scope.performedActions = []

      loadActions = (loader) ->
        pendingActions = []
        performedActions = []
        deferred = $q.defer()
        $scope.loading = true
        loader().then (list) ->
          for action in list.list
            if action.state == 'PENDING' or action.state == 'DISMISSED'
              pendingActions.push action
            else
              performedActions.unshift action
          if list.total > list.offset + list.size
            loadActions(list.next)
            deferred.notify pendingActions: pendingActions, performedActions: performedActions
          else
            $scope.loading = false
            deferred.resolve pendingActions: pendingActions, performedActions: performedActions
        deferred.promise

      assignActions = (result) ->
        $scope.pendingActions = result.pendingActions
        $scope.performedActions = result.performedActions

      loadActions($scope.batch.actions).then assignActions, (->), assignActions

      $scope.reload = ->
        loadActions($scope.batch.actions).then assignActions
    ]
  }
]