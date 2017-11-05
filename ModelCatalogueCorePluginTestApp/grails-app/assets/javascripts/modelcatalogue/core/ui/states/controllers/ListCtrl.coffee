angular.module('mc.core.ui.states.controllers.ListCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.ListCtrl', [
  '$scope', '$stateParams', '$state', 'list', 'names', 'catalogue', 'enhance', 'applicationTitle', 'actionRoleAccess',
  ($scope ,  $stateParams ,  $state ,  list ,  names ,  catalogue ,  enhance ,  applicationTitle, actionRoleAccess) ->
    $scope.actionRoleAccess = actionRoleAccess
    if $stateParams.resource
      applicationTitle  catalogue.getPlural($stateParams.resource)

    $scope.list                     = list
    $scope.title                    = catalogue.getPlural($stateParams.resource)
    $scope.natural                  = (name) -> if name then names.getNaturalName(name) else "General"
    $scope.resource                 = $stateParams.resource

#    getLastModelsKey = (status = $stateParams.status)->
#      "#{status ? 'finalized'}"

#    if $scope.resource == 'dataClass' || $scope.resource == 'model' || $scope.resource == 'dataModel'
#      if $rootScope.$$lastModels and $rootScope.$$lastModels[getLastModelsKey()]
#        if $rootScope.$$lastModels[getLastModelsKey()].element
#          $rootScope.$$lastModels[getLastModelsKey()].element.refresh().then (element) ->
#            $scope.element                = element
#            $scope.elementSelectedInTree  = $rootScope.$$lastModels[getLastModelsKey()]?.elementSelectedInTree
#            $scope.property               = $rootScope.$$lastModels[getLastModelsKey()]?.property
#          , ->
#            $scope.element                = if list.size > 0 then list.list[0]
#            $scope.elementSelectedInTree  = false
#            $scope.property               = 'contains'
#
#      else
#        $rootScope.$$lastModels       = {}
#        $scope.elementSelectedInTree  = false
#        $scope.element                = if list.size > 0 then list.list[0]
#        $scope.property               =  'contains'
#
#      $scope.onTreeviewSelected = (element) ->
#        $scope.element                  = element
#        $scope.elementSelectedInTree    = true
#        $rootScope.$$lastModels ?= {}
#        $rootScope.$$lastModels[getLastModelsKey()] = element: element, elementSelectedInTree: true, property: 'contains'
#
#      $scope.$on 'newVersionCreated', (ignored, element) ->
#        if element
#          $rootScope.$$lastModels ?= {}
#          $rootScope.$$lastModels[getLastModelsKey('draft')] = element: element, elementSelectedInTree: true, property: 'history'
#          $state.go '.', {status: 'draft'}, { reload: true }
#
#      $scope.$on 'catalogueElementFinalized', (ignored, element) ->
#        if element
#          if element.childOf.total == 0
#            $rootScope.$$lastModels ?= {}
#            $rootScope.$$lastModels[getLastModelsKey('finalized')] = element: element, elementSelectedInTree: true, property: 'history'
#            $state.go '.', {status: undefined}, { reload: true }
])
