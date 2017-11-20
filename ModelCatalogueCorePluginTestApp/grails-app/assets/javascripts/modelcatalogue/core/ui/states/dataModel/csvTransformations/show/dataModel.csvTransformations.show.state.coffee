angular.module('modelcatalogue.core.ui.states.dataModel.csvTransformations.show',
['modelcatalogue.core.ui.states.dataModel.csvTransformations.show.csvTransformationCtrl',
  'modelcatalogue.core.ui.states.dataModel.csvTransformations.show.template',
]).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'dataModel.csvTransformations.show', {
      url: '/{id:\\d+}'

      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/csvTransformation.html'
          controller: 'modelcatalogue.core.ui.states.dataModel.csvTransformations.show.csvTransformationCtrl'

        'navbar-left@':
          template: '<contextual-menu role="{{::actionRoleAccess.ROLE_ITEM_ACTION}}"></contextual-menu>'
          controller: 'modelcatalogue.core.ui.states.controllers.ElementCtrl'


      resolve:
        element: ['$stateParams','catalogueElementResource', ($stateParams, catalogueElementResource) ->
          $stateParams.resource = "csvTransformation"
          return catalogueElementResource('csvTransformation').get($stateParams.id)
        ]
    }

])
