angular.module('mc.core.ui.states.simple.actions.show', ['mc.core.ui.states.controllers.BatchCtrl']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'simple.actions.show', {
      url: '/{id:\\d+}'

      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/batch.html'
          controller: 'mc.core.ui.states.controllers.BatchCtrl'

        'navbar-left@':
          template: '<contextual-menu role="{{::actionRoleAccess.ROLE_ITEM_ACTION}}"></contextual-menu>'
          controller: 'mc.core.ui.states.controllers.BatchOnlyCtrl'

      resolve:
        element: ['$stateParams','catalogueElementResource', ($stateParams, catalogueElementResource) ->
          $stateParams.resource = "batch"
          return catalogueElementResource('batch').get($stateParams.id)
        ]

    }
])
