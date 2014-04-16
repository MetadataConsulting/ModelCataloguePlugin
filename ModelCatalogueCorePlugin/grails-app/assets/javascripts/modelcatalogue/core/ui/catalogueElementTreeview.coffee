angular.module('mc.core.ui.catalogueElementTreeview', ['mc.core.ui.catalogueElementTreeviewItem']).directive 'catalogueElementTreeview',  [-> {
    restrict: 'E'
    replace: true
    scope:
      element: '='
      descend: '='
      id:      '@'

    templateUrl: 'modelcatalogue/core/ui/catalogueElementTreeview.html'

    controller: ($scope) ->
      $scope.id = null if !$scope.id

  }
]