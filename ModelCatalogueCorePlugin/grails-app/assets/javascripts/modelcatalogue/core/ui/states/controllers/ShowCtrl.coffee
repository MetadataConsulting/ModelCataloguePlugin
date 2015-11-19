angular.module('mc.core.ui.states.controllers.ShowCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.ShowCtrl', [
  '$scope', '$stateParams', '$state', 'element', '$rootScope', 'enhance',
  ($scope ,  $stateParams ,  $state ,  element ,  $rootScope ,  enhance) ->

    listEnhancer = enhance.getEnhancer('list')

    $scope.element = element
    $scope.original = element
    $scope.elementAsList = listEnhancer.createSingletonList(element)
    $rootScope.elementToShow = element
    $scope.onTreeviewSelected = (element) ->
      if element.resource
        $scope.list = listEnhancer.createEmptyList(element.resource)
        $scope.resource = element.resource
        $rootScope.$broadcast 'redrawContextualActions'
        if angular.isFunction(element.content)
          element.content().then (newList) ->
            $scope.list = newList
            $rootScope.$broadcast 'redrawContextualActions'
      else
        $scope.list = undefined
        $scope.resource = undefined
        $rootScope.$broadcast 'redrawContextualActions'

      $scope.element = element
])