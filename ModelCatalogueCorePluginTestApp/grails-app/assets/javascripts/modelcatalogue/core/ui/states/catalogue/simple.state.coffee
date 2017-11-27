angular.module('modelcatalogue.core.ui.states.catalogue', [
  'modelcatalogue.core.ui.states.catalogue.actions',
  'modelcatalogue.core.ui.states.catalogue.favourites',
  'modelcatalogue.core.ui.states.catalogue.feedback',
  'modelcatalogue.core.ui.states.catalogue.resource'
]).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'simple', {
      abstract: true
      url: '/catalogue'
      templateUrl: '/modelcatalogue/core/ui/states/catalogue/simple.html'
    }

])
