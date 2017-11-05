angular.module('mc.core.ui.states.mc.csvTransformations.show', ['mc.core.ui.states.controllers.CsvTransformationCtrl']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc.csvTransformations.show', {
      url: '/{id:\\d+}'

      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/csvTransformation.html'
          controller: 'mc.core.ui.states.controllers.CsvTransformationCtrl'

        'navbar-left@':
          template: '<contextual-menu role="{{::actionRoleAccess.ROLE_ITEM_ACTION}}"></contextual-menu>'
          controller: 'mc.core.ui.states.controllers.ElementCtrl'


      resolve:
        element: ['$stateParams','catalogueElementResource', ($stateParams, catalogueElementResource) ->
          $stateParams.resource = "csvTransformation"
          return catalogueElementResource('csvTransformation').get($stateParams.id)
        ]
    }

])
