angular.module('modelcatalogue.core.ui.states.catalogue.resource', [
  'modelcatalogue.core.ui.states.catalogue.resource.list',
  'modelcatalogue.core.ui.states.catalogue.resource.show'
]).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'catalogue.resource', {
      abstract: true
      url: '/:resource'
      templateUrl: 'modelcatalogue/core/ui/state/parent.html'
    }
])
