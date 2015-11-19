angular.module('mc.core.ui.states.mc.dataArchitect', ['mc.core.ui.states.controllers']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state('mc.dataArchitect', {
      abstract: true,
      url: "/dataArchitect"
      templateUrl: 'modelcatalogue/core/ui/state/parent.html'
    })

])