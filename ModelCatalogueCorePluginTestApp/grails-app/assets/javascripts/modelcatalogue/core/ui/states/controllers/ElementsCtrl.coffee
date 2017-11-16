###
  Simple controller which passes the element resolved to the scope
###

angular.module('modelcatalogue.core.ui.states.controllers.ElementsCtrl', ['ui.router', 'mc.util.ui'])
.controller('modelcatalogue.core.ui.states.controllers.ElementsCtrl', [
  '$scope', 'elements', 'actionRoleAccess',
  ($scope ,  elements , actionRoleAccess ) ->
    $scope.actionRoleAccess = actionRoleAccess
    $scope.elements  = elements
])
