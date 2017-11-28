angular.module('modelcatalogue.core.ui.states.catalogue.actions.show', [
  'modelcatalogue.core.ui.states.catalogue.actions.show.BatchCtrl',
  'modelcatalogue.core.ui.states.catalogue.actions.show.BatchOnlyCtrl',
  'modelcatalogue.core.ui.states.catalogue.actions.show.batch.html'
]).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'catalogue.actions.show', {
      url: '/{id:\\d+}'

      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/batch.html'
          controller: 'modelcatalogue.core.ui.states.catalogue.actions.show.BatchCtrl'

        'navbar-left@':
          template: '<contextual-menu role="{{::actionRoleAccess.ROLE_ITEM_ACTION}}"></contextual-menu>'
          controller: 'modelcatalogue.core.ui.states.catalogue.actions.show.BatchOnlyCtrl'

      resolve:
        element: ['$stateParams','catalogueElementResource', ($stateParams, catalogueElementResource) ->
          $stateParams.resource = "batch"
          return catalogueElementResource('batch').get($stateParams.id)
        ]

    }
])
