###
  Simple controller which passes the element resolved to the scope
###

angular.module('mc.core.ui.states.controllers.DataModelCtrl', ['ui.router', 'mc.util.ui'])
.controller('mc.core.ui.states.controllers.DataModelCtrl', [
  '$scope', 'currentDataModel',
  ($scope ,  currentDataModel ) ->
    $scope.currentDataModel  = currentDataModel
])