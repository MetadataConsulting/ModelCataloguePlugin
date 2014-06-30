angular.module('mc.util.ui.contextualActions', ['mc.util.ui.actionButton']).directive 'contextualActions',  [-> {
  restrict: 'E'
  replace: true
  scope:
    context:    '='
    group:      '=?'
    size:       '@'
    iconsOnly:  '=?'


  templateUrl: 'modelcatalogue/util/ui/contextualActions.html'

  controller: ['$scope', 'actions', '$attrs', ($scope, actions) ->
    $scope.actions = actions.getActions($scope.context)

    $scope.$on 'userLoggedIn', -> $scope.actions = actions.getActions($scope.context)
    $scope.$on 'userLoggedOut', -> $scope.actions = actions.getActions($scope.context)

    $scope.$on 'redrawConceptualActions', -> $scope.actions = actions.getActions($scope.context)
  ]
}]