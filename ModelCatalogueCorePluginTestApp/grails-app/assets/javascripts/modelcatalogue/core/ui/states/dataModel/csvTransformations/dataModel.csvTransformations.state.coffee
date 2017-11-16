angular.module('mc.core.ui.states.dataModel.csvTransformations', ['mc.core.ui.states.dataModel.csvTransformations.show']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state('mc.csvTransformations', {
      abstract: true,
      url: "/transformations/csv"
      templateUrl: 'modelcatalogue/core/ui/state/parent.html'
    })


])
