###
  Not used anywhere.
###
angular.module('modelcatalogue.core.ui.states.controllers.FastActionsCtrl', ['ui.router', 'mc.util.ui',
  'modelcatalogue.core.ui.navigationRight.global.modalSearchForActions']).controller('modelcatalogue.core.ui.states.controllers.FastActionsCtrl', [
  '$scope', 'messages',
  ($scope ,  messages)->
    $scope.showFastActions = ->
      messages.prompt null, null, type: 'search-action'
])
