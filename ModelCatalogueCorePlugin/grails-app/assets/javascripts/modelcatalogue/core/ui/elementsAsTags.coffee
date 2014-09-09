angular.module('mc.core.ui.elementsAsTags', ['mc.util.names', 'ui.router']).directive 'elementsAsTags',  [-> {
    restrict: 'E'
    replace: true
    scope:
      elements: '='
    templateUrl: 'modelcatalogue/core/ui/elementsAsTags.html'

    controller: ['$scope', 'names', '$state', '$window', ($scope, names, $state, $window) ->
      $scope.openElementInNewWindow = (element) ->
        url = $state.href('mc.resource.show', {resource: names.getPropertyNameFromType(element.elementType), id: element.id})
        $window.open(url,'_blank')
        return

      $scope.removeItem = (index) ->
        $scope.elements.splice index, 1

      $scope.isString = angular.isString
    ]

  }
]