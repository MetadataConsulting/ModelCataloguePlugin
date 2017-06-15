angular.module('mc.core.ui.utils').directive 'ellipsis', -> {
  transclude: true
  replace: true
  scope:
    text: '@ellipsis'
    maxCharacters: '@?'
  templateUrl: '/mc/core/ui/utils/ellipsis.html'
  controller: ($scope) ->
    'ngInject'
    $scope.maxCharacters = 500 unless $scope.maxCharacters
    $scope.showFull = false

    $scope.show = (full) ->
      $scope.showFull = full
}
