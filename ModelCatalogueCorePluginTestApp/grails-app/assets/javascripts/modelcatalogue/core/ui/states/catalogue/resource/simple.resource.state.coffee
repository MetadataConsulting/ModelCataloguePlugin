angular.module('mc.core.ui.states.simple.resource', [
  'mc.core.ui.states.simple.resource.list',
  'mc.core.ui.states.simple.resource.show'
]).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'simple.resource', {
      abstract: true
      url: '/:resource'
      templateUrl: 'modelcatalogue/core/ui/state/parent.html'
    }
])
