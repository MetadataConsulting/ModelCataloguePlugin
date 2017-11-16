angular.module('modelcatalogue.core.ui.states.dataModel.search', ['modelcatalogue.core.ui.states.controllers.ListCtrl']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state('dataModel.search', {
      url: "/search/{q}",
      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/list.html'
          controller: 'modelcatalogue.core.ui.states.controllers.ListCtrl'

        'navbar-left@':
          template: '<contextual-menu role="{{::actionRoleAccess.ROLE_LIST_ACTION}}"></contextual-menu>'
          controller: 'modelcatalogue.core.ui.states.controllers.ListCtrl'
      resolve: {
        list: ['$stateParams','modelCatalogueSearch', ($stateParams, modelCatalogueSearch) ->
          $stateParams.resource = "searchResult"
          params = {}
          if $stateParams.dataModelId and $stateParams.dataModelId isnt 'catalogue'
            params.dataModel = $stateParams.dataModelId

          return modelCatalogueSearch($stateParams.q, params)
        ]
      },
    })

])
