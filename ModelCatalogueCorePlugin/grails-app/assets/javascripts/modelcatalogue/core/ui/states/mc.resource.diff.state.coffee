angular.module('mc.core.ui.states.mc.resource.diff', ['mc.core.ui.states.controllers.DiffCtrl']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc.resource.diff', {
      url: '/diff/{ids:(?:\\d+)(?:\\~\\d+)+}'
      templateUrl: 'modelcatalogue/core/ui/state/diff.html'
      resolve:
        elements: ['$stateParams','catalogueElementResource', '$q', ($stateParams, catalogueElementResource, $q) ->
          $q.all (catalogueElementResource($stateParams.resource).get(id) for id in $stateParams.ids.split('~'))
        ]
      controller: 'mc.core.ui.states.controllers.DiffCtrl'
    }
])