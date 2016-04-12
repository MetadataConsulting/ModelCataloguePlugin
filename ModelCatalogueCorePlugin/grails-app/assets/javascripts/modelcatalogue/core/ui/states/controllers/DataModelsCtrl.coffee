angular.module('mc.core.ui.states.controllers.DataModelsCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.DataModelsCtrl', [
  '$scope', '$state', '$stateParams', 'list', 'applicationTitle', 'catalogueElementResource', 'catalogue', 'names', '$timeout', 'messages',
  ($scope ,  $state ,  $stateParams ,  list ,  applicationTitle ,  catalogueElementResource ,  catalogue ,  names ,  $timeout ,  messages) ->

    original = list

    $scope.status = $stateParams.status
    $scope.type = $stateParams.type
    $scope.q = $stateParams.q

    $scope.list = list

    $scope.title = $state.current?.data?.applicationTitle

    $scope.search = (term) ->
      unless term
        $scope.list = original
      else
        catalogueElementResource($state.current?.data?.resource).search(term).then (results) ->
          $scope.list = results

    $scope.createElement = ->
      messages.prompt($state.current?.data?.createDialogTitle ? "Create", $state.current?.data?.createDialogBody ? '', $state.current?.data?.createDialogArgs).then (element)->
        element.show()

    $scope.dataModelOrDestination = (item) ->
      if catalogue.isInstanceOf item.elementType, 'dataModel'
        return item
      if catalogue.isInstanceOf item.elementType, 'relationship'
        return item.relation

      return item


    $scope.changeStatus = (newStatus) ->
      $state.go '.', {status: newStatus}
      $scope.status = newStatus

    $scope.getCurrentStatus = ->
      names.capitalize(($scope.status ? 'All').toLowerCase())

    $scope.getCurrentType = ->
      names.capitalize(($scope.type ? 'My').toLowerCase())

    $scope.showMyModels = ->
      $state.go '.', {type: undefined}
      $scope.type = undefined

    $scope.showAllModels = ->
      $state.go '.', {type: 'catalogue'}
      $scope.type = 'catalogue'

    $scope.$watch 'q', (q) ->
      $state.go '.', {q: q}

    $scope.$on 'newVersionCreated', ->
      $state.go '.', type: undefined

])
