angular.module('mc.core.ui.columnsConfiguration', []).directive 'columnsConfiguration',  [-> {
    restrict: 'E'
    replace: true
    scope:
      columns: '='

    templateUrl: 'modelcatalogue/core/ui/columnsConfiguration.html'

    controller: ['$scope' , ($scope) ->
       $scope.removeColumn = (index) ->
         return if $scope.columns.length <= 1
         $scope.columns.splice(index, 1)

       $scope.addColumn = (index, column = {header: 'ID', value: 'id', classes: 'col-md-2'}) ->
         $scope.columns.splice(index + 1, 0, angular.copy(column))
    ]
  }
]