angular.module('mc.core.ui.states.mc.csvTransformations', []).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state('mc.csvTransformations', {
      abstract: true,
      url: "/transformations/csv"
      templateUrl: 'modelcatalogue/core/ui/state/parent.html'
    })


])