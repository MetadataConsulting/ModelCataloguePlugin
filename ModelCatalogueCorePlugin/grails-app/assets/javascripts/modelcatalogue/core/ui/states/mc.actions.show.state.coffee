angular.module('mc.core.ui.states.mc.actions.show', ['mc.core.ui.states.controllers.BatchCtrl']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc.actions.show', {
      url: '/{id:\\d+}'
      templateUrl: 'modelcatalogue/core/ui/state/batch.html'
      resolve:
        element: ['$stateParams','catalogueElementResource', ($stateParams, catalogueElementResource) ->
          $stateParams.resource = "batch"
          return catalogueElementResource('batch').get($stateParams.id)
        ]

      controller: 'mc.core.ui.states.controllers.BatchCtrl'
    }
])