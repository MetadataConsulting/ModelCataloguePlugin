angular.module('mc.core.ui.states.mc.resource', []).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc.resource', {
      abstract: true
      url: '/:resource'
      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/parent.html'
        'navbar-left':
          template: '<contextual-menu></contextual-menu><div ui-view="navbar-left"></div>'
    }
])