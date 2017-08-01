#= require sensitivityLevels.val.coffee
angular.module('modelcatalogue.core.sections.detailSections.stewardship').controller('stewardshipCtrl', [
  'sensitivityLevels', '$scope',
  (sensitivityLevels, $scope) ->
    $scope.sensitivityLevels = sensitivityLevels
])
