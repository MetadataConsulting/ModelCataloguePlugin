angular.module('mc.core.ui.states.simple', []).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'simple', {
      abstract: true
      url: '/catalogue'
      templateUrl: 'modelcatalogue/core/ui/state/parent.html'
    }

])