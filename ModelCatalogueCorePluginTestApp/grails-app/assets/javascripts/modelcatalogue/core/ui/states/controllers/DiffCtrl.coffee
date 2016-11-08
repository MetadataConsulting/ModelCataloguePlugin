angular.module('mc.core.ui.states.controllers.DiffCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.DiffCtrl', [
  '$scope', '$stateParams', '$state', 'elements', 'applicationTitle',
  ($scope ,  $stateParams ,  $state ,  elements ,  applicationTitle) ->

    $scope.elements = elements
    applicationTitle "Comparison of #{((element.getLabel?.apply(element) ? element.name) for element in elements).join(' and ')}"

])