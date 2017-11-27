angular.module('modelcatalogue.core.ui.states.catalogue.resource.show', ['modelcatalogue.core.ui.states.controllers',
'modelcatalogue.core.ui.states.catalogue.resource.show.property']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'catalogue.resource.show', {
      url: '/{id:\\d+}'

      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/show.html'
          controller: 'modelcatalogue.core.ui.states.controllers.ShowCtrl'

        'navbar-left@':
          template: '<contextual-menu role="{{::actionRoleAccess.ROLE_ITEM_ACTION}}"></contextual-menu>'
          controller: 'modelcatalogue.core.ui.states.controllers.ElementCtrl'

      resolve:
        element: ['$stateParams','catalogueElementResource', ($stateParams, catalogueElementResource) ->
          catalogueElementResource($stateParams.resource).get($stateParams.id)
        ]
    }

])
