angular.module('mc.util.ui.sidenav', ['mc.util.ui.actions']).directive 'sidenav',  ['actions', (actions)-> {
  restrict: 'E'
  replace:  true

  templateUrl: 'modelcatalogue/util/ui/sidenav.html'

  controller: ['$scope', ($scope) ->

    updateActions = ->
      $scope.actions = actions.getActions($scope, actions.ROLE_SIDENAV)

    updateActions()

    $scope.$on 'userLoggedIn', updateActions
    $scope.$on 'userLoggedOut', updateActions
    $scope.$on 'redrawContextualActions', updateActions

  ]

}]