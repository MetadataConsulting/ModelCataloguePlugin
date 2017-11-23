#= require diffCtrl
angular.module('modelcatalogue.core.ui.states.dataModel.resource.diff',
['modelcatalogue.core.ui.states.dataModel.resource.diff.diffCtrl',
  'modelcatalogue.core.ui.states.dataModel.resource.diff.components.diffTable',
  'modelcatalogue.core.ui.states.dataModel.resource.diff.html'])
  .config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'dataModel.resource.diff', {
      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/diff.html'
          controller: 'modelcatalogue.core.ui.states.dataModel.resource.diff.diffCtrl'

        'navbar-left@':
          template: '<contextual-menu role="{{::actionRoleAccess.ROLE_ITEM_ACTION}}"></contextual-menu>'
          controller: 'modelcatalogue.core.ui.states.controllers.ElementsCtrl'

      url: '/diff/{ids:(?:\\d+)(?:\\|\\d+)+}'

      resolve:
        elements: ['$stateParams','catalogueElementResource', '$q', ($stateParams, catalogueElementResource, $q) ->
          $q.all (catalogueElementResource($stateParams.resource).get(id) for id in $stateParams.ids.split('|'))
        ]

    }
])
