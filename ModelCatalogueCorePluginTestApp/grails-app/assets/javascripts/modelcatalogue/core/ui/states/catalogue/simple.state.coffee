angular.module('mc.core.ui.states.simple', [
  'mc.core.ui.states.simple.actions',
  'mc.core.ui.states.simple.favorites',
  'mc.core.ui.states.simple.feedback',
  'mc.core.ui.states.simple.resource'
]).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'simple', {
      abstract: true
      url: '/catalogue'
      templateUrl: '/mc/core/ui/states/simple.html'
    }

])
