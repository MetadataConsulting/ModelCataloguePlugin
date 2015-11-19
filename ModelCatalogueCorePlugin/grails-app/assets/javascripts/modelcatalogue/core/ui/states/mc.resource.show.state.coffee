angular.module('mc.core.ui.states.mc.resource.show', ['mc.core.ui.states.controllers.ShowCtrl']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc.resource.show', {
      url: '/{id:\\d+}'

      templateUrl: 'modelcatalogue/core/ui/state/show.html'

      resolve:
        element: ['$stateParams','catalogueElementResource', ($stateParams, catalogueElementResource) ->
          catalogueElementResource($stateParams.resource).get($stateParams.id)
        ]
      onExit: ['$rootScope', ($rootScope) ->
        $rootScope.elementToShow = null
      ]

      controller: 'mc.core.ui.states.controllers.ShowCtrl'
    }

])