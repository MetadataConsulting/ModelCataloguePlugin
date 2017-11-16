###
  Simple controller which passes the element resolved to the scope
###

angular.module('modelcatalogue.core.ui.states.dataModel')
.controller('modelcatalogue.core.ui.states.dataModel.dataModelCtrl', [
  '$scope', 'currentDataModel', 'actionRoleAccess',
  ($scope ,  currentDataModel, actionRoleAccess ) ->
    $scope.currentDataModel  = currentDataModel
    $scope.actionRoleAccess = actionRoleAccess
])
