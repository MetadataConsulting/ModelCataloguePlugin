angular.module('mc.core.ui.states.controllers.UserCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.UserCtrl', [
  '$scope', 'security',
  ($scope ,  security)->
    $scope.logout = ->
      security.logout()
    $scope.login = ->
      security.requireLogin()
])
