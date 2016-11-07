angular.module('mc.core.ui.catalogueElementIcon', ['mc.core.catalogue']).directive 'catalogueElementIcon',  ['$compile', 'catalogue', ($compile, catalogue)-> {
  restrict: 'E'
  template: """<span ng-class="getIcon()"></span>"""
  replace: true
  scope:
    type: '='


  controller: ['$scope',  '$attrs', ($scope) ->
    $scope.getIcon = -> catalogue.getIcon($scope.type ? 'catalogueElement')
  ]

}]