angular.module('mc.core.ui.states.simple.actions', []).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state('simple.actions', {
      abstract: true,
      url: "/actions/batch"
      templateUrl: 'modelcatalogue/core/ui/state/parent.html'
    })

])