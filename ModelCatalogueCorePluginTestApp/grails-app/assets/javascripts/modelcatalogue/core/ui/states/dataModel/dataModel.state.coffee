angular.module('modelcatalogue.core.ui.states.dataModel', [
  'modelcatalogue.core.ui.states.dataModel.csvTransformations',
  'modelcatalogue.core.ui.states.dataModel.resource',
  'modelcatalogue.core.ui.states.dataModel.search',
  'modelcatalogue.core.ui.states.dataModel.components.catalogueElementTreeview'

  'ui.router', 'mc.util.ui' # for Ctrl
  ])
.config ($stateProvider) ->
  'ngInject'

  $stateProvider.state 'dataModel', {
    abstract: true
    url: '/{dataModelId:[0-9]+}'
    views:
      "":
        templateUrl: '/modelcatalogue/core/ui/states/dataModel/dataModel.html'
        controller: 'modelcatalogue.core.ui.states.dataModel.dataModelTreeCtrl'

      'navbar-left@':
        template: '<contextual-menu></contextual-menu>'

      'navbar-right@':
        template: '<contextual-menu role="{{::actionRoleAccess.ROLE_NAVIGATION_RIGHT_ACTION}}" right="true"></contextual-menu>'
        controller: 'modelcatalogue.core.ui.states.dataModel.dataModelCtrl'

    resolve:
      currentDataModel: ['catalogue', '$rootScope', '$stateParams', '$q', 'catalogueElementResource',
        (catalogue, $rootScope, $stateParams, $q, catalogueElementResource) ->
          deferred = $q.defer()

          catalogueElementResource('dataModel').get($stateParams.dataModelId).then (dataModel) ->
            deferred.resolve(dataModel)

          deferred.promise
      ]

      lastSelectedElementHolder: -> {element: null}
  }
