angular.module('modelcatalogue.core.ui.states.catalogue.actions.show.BatchOnlyCtrl', ['ui.router', 'mc.util.ui']).controller('modelcatalogue.core.ui.states.catalogue.actions.show.BatchOnlyCtrl', [
  '$scope', '$stateParams', '$state', 'element', 'applicationTitle', 'actionRoleAccess',
  ($scope ,  $stateParams ,  $state ,  element ,  applicationTitle, actionRoleAccess) ->
    $scope.batch = element
    $scope.actionRoleAccess = actionRoleAccess

    applicationTitle "Actions in batch #{element.name}"
])
