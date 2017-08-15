###
  Simple controller which passes the element resolved to the scope
###

angular.module('mc.core.ui.states.controllers.DataModelCtrl', ['ui.router', 'mc.util.ui', 'mc.util.MessagingClient'])
.controller('mc.core.ui.states.controllers.DataModelCtrl', [
  '$scope', 'currentDataModel', '$rootScope', 'MessagingClient','$q',
  ($scope ,  currentDataModel , $rootScope, MessagingClient, $q) ->

    $q.when(MessagingClient.connect()).then ->
      $rootScope.$security.refreshUserData(currentDataModel.id)
      $rootScope.currentDataModel  = currentDataModel

])