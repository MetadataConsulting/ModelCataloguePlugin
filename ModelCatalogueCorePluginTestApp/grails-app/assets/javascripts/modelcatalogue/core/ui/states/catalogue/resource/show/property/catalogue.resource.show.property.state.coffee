angular.module('modelcatalogue.core.ui.states.catalogue.resource.show.property', []).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'catalogue.resource.show.property', {url: '/:property?page&sort&order&max&q'}

])
