angular.module('mc.core.ui.states.mc.resource.show.property', []).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc.resource.show.property', {url: '/:property?page&sort&order&max&q'}

])