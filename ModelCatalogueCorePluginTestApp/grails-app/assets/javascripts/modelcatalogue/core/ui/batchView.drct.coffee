angular.module('mc.core.ui.batchView', ['modelcatalogue.core.enhancersConf.catalogueElementEnhancer', 'modelcatalogue.core.enhancersConf.listReferenceEnhancer', 'modelcatalogue.core.enhancersConf.listEnhancer','mc.util.names', 'mc.util.messages', 'mc.core.ui.columns', 'mc.util.ui.actions', 'ui.router', 'mc.core.ui.catalogueElementProperties']).directive 'batchView',  [-> {
    restrict: 'E'
    replace: true
    scope:
      batch: '='
      property: '=?'
      id: '@'

    templateUrl: 'modelcatalogue/core/ui/batchView.html'

    controller: ['$scope','$q', '$window', 'names', '$timeout', 'actionRoleAccess', ($scope, $q, $window, names, $timeout, actionRoleAccess) ->
      $scope.actionRoleAccess = actionRoleAccess
      $scope.getType = (action) ->
        return 'info'    if not action
        return 'warning' if action.highlighted
        return 'info'    if action.state == 'PERFORMING'
        return 'danger'  if action.state == 'DISMISSED'
        return 'success' if action.state == 'PERFORMED'
        return 'danger'  if action.state == 'FAILED'
        return 'success' if action.state == 'PENDING'
        return 'info'

      $scope.pendingActions = []
      $scope.performedActions = []
      $scope.loading = true

      $scope.natural = (name)->
        names.getNaturalName(name)

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
            $timeout (->
              loadActions(list.next, pendingActions, performedActions).then ->
                deferred.resolve pendingActions: pendingActions, performedActions: performedActions), 300
          else
            deferred.resolve pendingActions: pendingActions, performedActions: performedActions
        deferred.promise

      assignActions = (result) ->
        $scope.pendingActions = result.pendingActions
        $scope.performedActions = result.performedActions

      loadActions($scope.batch.actions).then(assignActions, (->), assignActions).then ->
        $scope.loading = false

      $scope.batch.$$reload = ->
        loadActions($scope.batch.actions).then assignActions


      $scope.highlight = (idsToRoles)->
        for action in $scope.pendingActions
          action.highlighted = idsToRoles[action.id]
        for action in $scope.performedActions
          action.highlighted = idsToRoles[action.id]

        ids = []

        for id, ignored of idsToRoles when id != 'length'
          ids.push parseInt(id, 10)

        if ids.length > 0
          $window.scrollTo 0, $("#action-#{Math.min(ids...)}").offset().top - 70
    ]
  }
]
