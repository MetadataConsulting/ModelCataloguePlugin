angular.module('mc.core.ui.states.controllers.PanelsCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.PanelsCtrl', [
  '$scope', '$state', 'list', 'applicationTitle', 'catalogueElementResource', 'catalogue', 'messages',
  ($scope ,  $state ,  list ,  applicationTitle ,  catalogueElementResource ,  catalogue ,  messages) ->

    $scope.list = list

    $scope.title = $state.current?.data?.applicationTitle

    $scope.createElement = ->
      messages.prompt($state.current?.data?.createDialogTitle ? "Create", $state.current?.data?.createDialogBody ? '', $state.current?.data?.createDialogArgs).then (element)->
        element.show()

])