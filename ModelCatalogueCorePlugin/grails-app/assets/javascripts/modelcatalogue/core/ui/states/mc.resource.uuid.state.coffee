angular.module('mc.core.ui.states.mc.resource.uuid', ['mc.core.ui.states.controllers.ShowCtrl']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc.resource.uuid', {
      url: '/uuid/:uuid'

      templateUrl: 'modelcatalogue/core/ui/state/show.html'

      resolve:
        element: ['$stateParams','catalogueElementResource', ($stateParams, catalogueElementResource) ->
          catalogueElementResource($stateParams.resource).getByUUID($stateParams.uuid)
        ]

      controller: 'mc.core.ui.states.controllers.ShowCtrl'
    }

])