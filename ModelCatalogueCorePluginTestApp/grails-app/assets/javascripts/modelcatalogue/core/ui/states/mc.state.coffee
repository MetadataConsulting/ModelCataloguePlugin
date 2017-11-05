angular.module('mc.core.ui.states.mc', ['mc.core.ui.states.controllers.DataModelTreeCtrl'])
.config ($stateProvider) ->
  'ngInject'

  $stateProvider.state 'mc', {
    abstract: true
    url: '/{dataModelId:[0-9]+}'
    views:
      "":
        templateUrl: '/mc/core/ui/states/mc.html'
        controller: 'mc.core.ui.states.controllers.DataModelTreeCtrl'

      'navbar-left@':
        template: '<contextual-menu></contextual-menu>'

      'navbar-right@':
        template: '<contextual-menu role="{{::actionRoleAccess.ROLE_NAVIGATION_RIGHT_ACTION}}" right="true"></contextual-menu>'
        controller: 'mc.core.ui.states.controllers.DataModelCtrl'

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
