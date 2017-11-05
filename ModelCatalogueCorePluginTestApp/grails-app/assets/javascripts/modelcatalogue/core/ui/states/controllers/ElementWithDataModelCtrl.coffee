###
  Simple controller which passes the element resolved to the scope
###

angular.module('mc.core.ui.states.controllers.ElementWithDataModelCtrl', ['ui.router', 'mc.util.ui'])
.controller('mc.core.ui.states.controllers.ElementWithDataModelCtrl', [
  '$scope', 'element', 'currentDataModel', 'actionRoleAccess',
  ($scope ,  element ,  currentDataModel, actionRoleAccess) ->
    $scope.actionRoleAccess = actionRoleAccess
    $scope.element = element
    $scope.currentDataModel = currentDataModel
])
