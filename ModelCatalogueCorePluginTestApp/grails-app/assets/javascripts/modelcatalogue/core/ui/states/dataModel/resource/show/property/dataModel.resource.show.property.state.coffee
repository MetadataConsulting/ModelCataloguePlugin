angular.module('modelcatalogue.core.ui.states.dataModel.resource.show.property', []).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc.resource.show.property', {
      url: '/:property?page&sort&order&max&q&focused&path'

      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/show.html'
          controller: 'modelcatalogue.core.ui.states.controllers.ShowCtrl'
    }

])
