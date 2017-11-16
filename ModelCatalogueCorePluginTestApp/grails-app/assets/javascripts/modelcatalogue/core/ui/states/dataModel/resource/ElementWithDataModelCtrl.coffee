###
  Simple controller which passes the element resolved to the scope
###

angular.module('modelcatalogue.core.ui.states.dataModel.resource.ElementWithDataModelCtrl', ['ui.router', 'mc.util.ui'])
.controller('modelcatalogue.core.ui.states.dataModel.resource.ElementWithDataModelCtrl', [
  '$scope', 'element', 'currentDataModel', 'actionRoleAccess',
  ($scope ,  element ,  currentDataModel, actionRoleAccess) ->
    $scope.actionRoleAccess = actionRoleAccess
    $scope.element = element
    $scope.currentDataModel = currentDataModel
])
