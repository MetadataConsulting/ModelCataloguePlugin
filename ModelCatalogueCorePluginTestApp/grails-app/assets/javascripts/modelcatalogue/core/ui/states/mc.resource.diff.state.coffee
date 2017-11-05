angular.module('mc.core.ui.states.mc.resource.diff', ['mc.core.ui.states.controllers.DiffCtrl']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc.resource.diff', {
      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/diff.html'
          controller: 'mc.core.ui.states.controllers.DiffCtrl'

        'navbar-left@':
          template: '<contextual-menu role="{{::actionRoleAccess.ROLE_ITEM_ACTION}}"></contextual-menu>'
          controller: 'mc.core.ui.states.controllers.ElementsCtrl'

      url: '/diff/{ids:(?:\\d+)(?:\\|\\d+)+}'

      resolve:
        elements: ['$stateParams','catalogueElementResource', '$q', ($stateParams, catalogueElementResource, $q) ->
          $q.all (catalogueElementResource($stateParams.resource).get(id) for id in $stateParams.ids.split('|'))
        ]

    }
])
