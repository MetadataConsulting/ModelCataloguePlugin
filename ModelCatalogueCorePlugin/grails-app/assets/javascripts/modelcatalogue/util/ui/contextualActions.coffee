angular.module('mc.util.ui.contextualActions', ['mc.util.ui.bs.actionButton']).directive 'contextualActions',  [-> {
  restrict: 'E'
  replace: true
  scope:
    context: '='

  templateUrl: 'modelcatalogue/util/ui/contextualActions.html'

  controller: ['$scope', 'actions', ($scope, actions) ->
    $scope.actions = actions.getActions($scope.context)
  ]
}]