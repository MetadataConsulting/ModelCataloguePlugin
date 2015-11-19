angular.module('mc.core.ui.states.controllers.ShowCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.ShowCtrl', [
  '$scope', '$stateParams', '$state', 'element', '$rootScope', 'enhance',
  ($scope ,  $stateParams ,  $state ,  element ,  $rootScope ,  enhance) ->

    listEnhancer = enhance.getEnhancer('list')

    $scope.element = element
    $scope.original = element
    $scope.elementAsList = listEnhancer.createSingletonList(element)
    $rootScope.elementToShow = element

])