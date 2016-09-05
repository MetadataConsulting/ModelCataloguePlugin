angular.module('mc.core.ui.states.mc.resource.show.property', []).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc.resource.show.property', {
      url: '/:property?page&sort&order&max&q&focused&path'

      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/show.html'
          controller: 'mc.core.ui.states.controllers.ShowCtrl'
    }

])
