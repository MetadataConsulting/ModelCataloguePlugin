angular.module('mc.core.ui.states.mc.dataArchitect.showMetadataRelations', ['mc.core.ui.states.controllers.ListCtrl']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc.dataArchitect.showMetadataRelations', {
      url: "/showMetadataRelations/{keyOne}/{keyTwo}",
      templateUrl: 'modelcatalogue/core/ui/state/list.html'
      resolve:
        list: ['$stateParams', 'modelCatalogueDataArchitect', ($stateParams, modelCatalogueDataArchitect) ->
          $stateParams.resource = "newRelationships"
          # it's safe to call top level for each controller, only model controller will respond on it
          return modelCatalogueDataArchitect.findRelationsByMetadataKeys($stateParams.keyOne, $stateParams.keyTwo)
        ]

      controller: 'mc.core.ui.states.controllers.ListCtrl'
    }

])