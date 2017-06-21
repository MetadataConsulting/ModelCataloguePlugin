#= require sensitivityLevels.val.coffee
angular.module('mc.core.stewardship').controller('stewardshipCtrl', [
  'sensitivityLevels', '$scope',
  (sensitivityLevels, $scope) ->
    $scope.sensitivityLevels = sensitivityLevels
])
