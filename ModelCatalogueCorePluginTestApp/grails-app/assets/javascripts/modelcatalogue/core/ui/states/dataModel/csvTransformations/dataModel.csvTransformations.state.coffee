angular.module('modelcatalogue.core.ui.states.dataModel.csvTransformations', ['modelcatalogue.core.ui.states.dataModel.csvTransformations.show',

  'ui.router', 'mc.util.ui']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state('dataModel.csvTransformations', {
      abstract: true,
      url: "/transformations/csv"
      templateUrl: 'modelcatalogue/core/ui/state/parent.html'
    })


])
