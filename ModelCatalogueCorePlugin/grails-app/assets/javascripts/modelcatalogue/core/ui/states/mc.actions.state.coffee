angular.module('mc.core.ui.states.mc.actions', []).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state('mc.actions', {
      abstract: true,
      url: "/actions/batch"
      templateUrl: 'modelcatalogue/core/ui/state/parent.html'
    })

])