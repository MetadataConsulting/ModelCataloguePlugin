angular.module('modelcatalogue.core.ui.states.dataModel.resource.diff.diffCtrl',
['ui.router', 'mc.util.ui']).controller('modelcatalogue.core.ui.states.dataModel.resource.diff.diffCtrl', [
  '$scope', '$stateParams', '$state', 'elements', 'applicationTitle',
  ($scope ,  $stateParams ,  $state ,  elements ,  applicationTitle) ->

    $scope.elements = elements
    applicationTitle "Comparison of #{((element.getLabel?.apply(element) ? element.name) for element in elements).join(' and ')}"

])
