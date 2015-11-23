angular.module('mc.core.ui.states.mc.actions.show', ['mc.core.ui.states.controllers.BatchCtrl']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc.actions.show', {
      url: '/{id:\\d+}'

      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/batch.html'
          controller: 'mc.core.ui.states.controllers.BatchCtrl'

        'navbar-left@':
          template: '<contextual-menu role="item"></contextual-menu>'
          controller: 'mc.core.ui.states.controllers.ElementCtrl'

      resolve:
        element: ['$stateParams','catalogueElementResource', ($stateParams, catalogueElementResource) ->
          $stateParams.resource = "batch"
          return catalogueElementResource('batch').get($stateParams.id)
        ]

    }
])