angular.module('mc.core.ui.states.mc.csvTransformations.show', ['mc.core.ui.states.controllers.CsvTransformationCtrl']).config(['$stateProvider', ($stateProvider) ->
  
    $stateProvider.state 'mc.csvTransformations.show', {
      url: '/{id:\\d+}'
      templateUrl: 'modelcatalogue/core/ui/state/csvTransformation.html'
      resolve:
        element: ['$stateParams','catalogueElementResource', ($stateParams, catalogueElementResource) ->
          $stateParams.resource = "csvTransformation"
          return catalogueElementResource('csvTransformation').get($stateParams.id)
        ]

      controller: 'mc.core.ui.states.controllers.CsvTransformationCtrl'
    }

])