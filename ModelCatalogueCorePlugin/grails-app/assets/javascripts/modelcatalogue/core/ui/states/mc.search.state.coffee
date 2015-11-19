angular.module('mc.core.ui.states.mc.search', ['mc.core.ui.states.controllers.ListCtrl']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state('mc.search', {
      url: "/search/{q}",
      templateUrl: 'modelcatalogue/core/ui/state/list.html'
      resolve: {
        list: ['$stateParams','modelCatalogueSearch', ($stateParams, modelCatalogueSearch) ->
          $stateParams.resource = "searchResult"
          params = {}
          if $stateParams.dataModelId and $stateParams.dataModelId isnt 'catalogue'
            params.dataModel = $stateParams.dataModelId

          return modelCatalogueSearch($stateParams.q, params)
        ]
      },
      controller: 'mc.core.ui.states.controllers.ListCtrl'
    })

])