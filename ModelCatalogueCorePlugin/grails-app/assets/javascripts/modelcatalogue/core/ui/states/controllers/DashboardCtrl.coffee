angular.module('mc.core.ui.states.controllers.DashboardCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.DashboardCtrl', [
  '$rootScope', '$scope', '$stateParams', '$state', 'security', 'catalogue', 'modelCatalogueApiRoot', 'user', 'messages', 'applicationTitle', 'names', 'statistics',
  ($rootScope ,  $scope ,  $stateParams ,  $state ,  security ,  catalogue ,  modelCatalogueApiRoot ,  user ,  messages ,  applicationTitle ,  names ,  statistics) ->

    applicationTitle "Model Catalogue"

    angular.extend $scope, statistics

    updateDashboard = (user) ->
      $scope.user = user
      if user?.id
        $state.go 'dataModels'

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

    $scope.image = (relativePath) ->
      lastIndex = security.contextPath.lastIndexOf('/')

      if lastIndex != -1 and lastIndex + 1 == security.contextPath.length
    # context path already ends with slash
        "#{security.contextPath}assets#{relativePath}"
      else
    # context path doesn't end with slash
        "#{security.contextPath}/assets#{relativePath}"

])