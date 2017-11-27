angular.module('mc.core.ui.states.simple.actions', [
  'mc.core.ui.states.simple.actions.show'
]).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state('simple.actions', {
      abstract: true,
      url: "/actions/batch"
      templateUrl: 'modelcatalogue/core/ui/state/parent.html'
    })

])
