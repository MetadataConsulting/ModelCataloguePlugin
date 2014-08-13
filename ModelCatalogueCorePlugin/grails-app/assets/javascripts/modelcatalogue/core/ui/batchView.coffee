angular.module('mc.core.ui.batchView', ['mc.core.catalogueElementEnhancer', 'mc.core.listReferenceEnhancer', 'mc.core.listEnhancer', 'mc.util.names', 'mc.util.messages', 'mc.core.ui.columns', 'mc.util.ui.actions', 'ui.router', 'mc.core.ui.catalogueElementProperties']).directive 'batchView',  [-> {
    restrict: 'E'
    replace: true
    scope:
      batch: '='
      property: '=?'
      id: '@'

    templateUrl: 'modelcatalogue/core/ui/batchView.html'

    controller: ['$scope','$q', '$window', ($scope, $q, $window) ->
      $scope.getType = (action) ->
        return 'info'    if not action
        return 'warning' if action.higlighted
        return 'info'    if action.state == 'PERFORMING'
        return 'danger'  if action.state == 'DISMISSED'
        return 'success' if action.state == 'PERFORMED'
        return 'danger'  if action.state == 'FAILED'
        return 'success' if action.state == 'PENDING'
        return 'info'

      $scope.pendingActions = []
      $scope.performedActions = []
      $scope.loading = true

      loadActions = (loader, pendingActions = [], performedActions = []) ->
        deferred = $q.defer()
        loader().then (list) ->
          for action in list.list
            if action.state == 'PENDING' or action.state == 'DISMISSED'
              pendingActions.push action
            else
              performedActions.unshift action
          if list.total > list.offset + list.size
            deferred.notify pendingActions: pendingActions, performedActions: performedActions
            loadActions(list.next, pendingActions, performedActions).then ->
              deferred.resolve pendingActions: pendingActions, performedActions: performedActions
          else
            deferred.resolve pendingActions: pendingActions, performedActions: performedActions
        deferred.promise

      assignActions = (result) ->
        $scope.pendingActions = result.pendingActions
        $scope.performedActions = result.performedActions

      loadActions($scope.batch.actions).then(assignActions, (->), assignActions).then ->
        $scope.loading = false

      $scope.reload = ->
        loadActions($scope.batch.actions).then assignActions


      $scope.highlight = (ids)->
        for action in $scope.pendingActions
          action.higlighted = action.id in ids
        for action in $scope.performedActions
          action.higlighted = action.id in ids

        if ids.length > 0
          $window.scrollTo 0, $("#action-#{Math.min(ids...)}").offset().top - 70
    ]
  }
]