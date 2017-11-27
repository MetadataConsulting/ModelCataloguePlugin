angular.module('mc.core.ui.states.simple.resource.show', ['modelcatalogue.core.ui.states.controllers',
'mc.core.ui.states.simple.resource.show.property']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'simple.resource.show', {
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
