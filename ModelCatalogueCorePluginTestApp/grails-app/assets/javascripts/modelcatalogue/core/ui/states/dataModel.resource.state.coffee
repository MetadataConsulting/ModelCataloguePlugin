angular.module('mc.core.ui.states.dataModel.resource', []).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc.resource', {
      abstract: true
      url: '/:resource'
      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/parent.html'
    }
])
