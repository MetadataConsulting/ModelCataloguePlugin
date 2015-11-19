angular.module('mc.core.ui.states.mc.dataArchitect.metadataKeyCheck', ['mc.core.ui.states.controllers.ListCtrl']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc.dataArchitect.metadataKeyCheck', {
      url: "/metadataKey/{metadata}",
      templateUrl: 'modelcatalogue/core/ui/state/list.html'
      resolve:
        list: ['$stateParams', 'modelCatalogueDataArchitect', ($stateParams, modelCatalogueDataArchitect) ->
          $stateParams.resource = "dataElement"
          # it's safe to call top level for each controller, only model controller will respond on it
          return modelCatalogueDataArchitect.metadataKeyCheck($stateParams.metadata)
        ]

      controller: 'mc.core.ui.states.controller.ListCtrl'
    }

])