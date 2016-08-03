angular.module('mc.core.ui.states.controllers.DashboardCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.DashboardCtrl', [
  '$rootScope', '$scope', '$stateParams', '$state', 'security', 'catalogue', 'modelCatalogueApiRoot', 'user', 'messages', 'applicationTitle', 'names',
  ($rootScope ,  $scope ,  $stateParams ,  $state ,  security ,  catalogue ,  modelCatalogueApiRoot ,  user ,  messages ,  applicationTitle ,  names ) ->

    applicationTitle "Model Catalogue"

    updateDashboard = (user) ->
      $scope.user = user
      if user?.id
        $state.go 'dataModels', {}, {location: 'replace'}

    $scope.$on('userLoggedIn', (ignored, user) ->
      if user?.error
        updateDashboard undefined
      else
        updateDashboard user
    )

    $scope.convert = ->
      messages.prompt('', '', {type: 'convert-with-value-domain'})

    $scope.validate = ->
      messages.prompt('', '', {type: 'validate-value-by-domain'})

    if security.allowRegistration
      $scope.registrationUrl = "#{security.contextPath}/register/"

    $scope.create = (what) ->
      dialogType = "create-#{what}"
      if not messages.hasPromptFactory(dialogType)
        dialogType = "edit-#{what}"
      messages.prompt("New #{names.getNaturalName(what)}", '', {type: dialogType, create: what}).then (element)->
        element.show()

    if user != ''
      updateDashboard(user)
    else
      $scope.user = user

    $scope.welcome = modelcatalogue.welcome
])
