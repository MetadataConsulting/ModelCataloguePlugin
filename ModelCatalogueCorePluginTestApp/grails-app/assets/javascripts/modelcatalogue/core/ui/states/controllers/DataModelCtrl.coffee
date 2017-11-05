###
  Simple controller which passes the element resolved to the scope
###

angular.module('mc.core.ui.states.controllers.DataModelCtrl', ['ui.router', 'mc.util.ui'])
.controller('mc.core.ui.states.controllers.DataModelCtrl', [
  '$scope', 'currentDataModel', 'actionRoleAccess',
  ($scope ,  currentDataModel, actionRoleAccess ) ->
    $scope.currentDataModel  = currentDataModel
    $scope.actionRoleAccess = actionRoleAccess
])
