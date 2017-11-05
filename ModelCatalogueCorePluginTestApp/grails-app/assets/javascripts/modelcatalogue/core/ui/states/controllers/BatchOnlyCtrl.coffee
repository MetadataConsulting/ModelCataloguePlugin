angular.module('mc.core.ui.states.controllers.BatchOnlyCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.BatchOnlyCtrl', [
  '$scope', '$stateParams', '$state', 'element', 'applicationTitle', 'actionRoleAccess',
  ($scope ,  $stateParams ,  $state ,  element ,  applicationTitle, actionRoleAccess) ->
    $scope.batch = element
    $scope.actionRoleAccess = actionRoleAccess

    applicationTitle "Actions in batch #{element.name}"
])
