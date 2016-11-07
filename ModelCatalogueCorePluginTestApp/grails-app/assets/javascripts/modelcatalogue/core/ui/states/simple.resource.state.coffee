angular.module('mc.core.ui.states.simple.resource', []).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'simple.resource', {
      abstract: true
      url: '/:resource'
      templateUrl: 'modelcatalogue/core/ui/state/parent.html'
    }
])