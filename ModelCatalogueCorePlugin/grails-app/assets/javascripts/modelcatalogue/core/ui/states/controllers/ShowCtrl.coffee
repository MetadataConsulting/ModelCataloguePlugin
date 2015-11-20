angular.module('mc.core.ui.states.controllers.ShowCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.ShowCtrl', [
  '$scope', '$stateParams', '$state', 'element',
  ($scope ,  $stateParams ,  $state ,  element) ->

    if (not $stateParams.dataModelId or $stateParams.dataModelId == 'catalogue') and element.getDataModelId()
      $state.go '.', {dataModelId: element.getDataModelId()}, {reload: true}
      return

    $scope.element = element
    $scope.original = element
])