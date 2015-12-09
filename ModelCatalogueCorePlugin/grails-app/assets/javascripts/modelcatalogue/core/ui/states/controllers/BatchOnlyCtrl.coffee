angular.module('mc.core.ui.states.controllers.BatchOnlyCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.BatchOnlyCtrl', [
  '$scope', '$stateParams', '$state', 'element', 'applicationTitle',
  ($scope ,  $stateParams ,  $state ,  element ,  applicationTitle) ->
    $scope.batch = element

    applicationTitle "Actions in batch #{element.name}"
])