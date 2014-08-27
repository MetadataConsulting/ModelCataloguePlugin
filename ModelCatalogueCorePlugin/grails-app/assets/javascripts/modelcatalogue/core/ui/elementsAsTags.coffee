angular.module('mc.core.ui.elementsAsTags', []).directive 'elementsAsTags',  [-> {
    restrict: 'E'
    replace: true
    scope:
      elements: '='
    templateUrl: 'modelcatalogue/core/ui/elementsAsTags.html'

    controller: ['$scope', ($scope) ->
        $scope.removeItem = (index) ->
          $scope.elements.splice index, 1
    ]

  }
]