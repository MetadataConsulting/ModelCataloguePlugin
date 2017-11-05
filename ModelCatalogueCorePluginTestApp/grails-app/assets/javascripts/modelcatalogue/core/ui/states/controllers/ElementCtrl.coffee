###
  Simple controller which passes the element resolved to the scope
###

angular.module('mc.core.ui.states.controllers.ElementCtrl', ['ui.router', 'mc.util.ui'])
.controller('mc.core.ui.states.controllers.ElementCtrl', [
  '$scope', 'element', 'actionRoleAccess',
  ($scope ,  element, actionRoleAccess ) ->
    $scope.element  = element
    $scope.actionRoleAccess = actionRoleAccess
])
