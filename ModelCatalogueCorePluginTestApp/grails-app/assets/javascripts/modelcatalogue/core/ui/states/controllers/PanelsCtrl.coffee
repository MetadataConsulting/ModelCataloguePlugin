angular.module('mc.core.ui.states.controllers.PanelsCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.PanelsCtrl', [
  '$scope', '$state', 'list', 'applicationTitle', 'catalogueElementResource', 'catalogue', 'messages',
  ($scope ,  $state ,  list ,  applicationTitle ,  catalogueElementResource ,  catalogue ,  messages) ->

    original = list

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

])