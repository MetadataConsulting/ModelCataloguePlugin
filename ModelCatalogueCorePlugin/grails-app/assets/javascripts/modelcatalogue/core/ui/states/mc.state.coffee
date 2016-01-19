angular.module('mc.core.ui.states.mc', ['mc.core.ui.states.bs.mc.html', 'mc.core.ui.states.controllers.DataModelTreeCtrl'])
.config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc', {
      abstract: true
      url: '/{dataModelId:[0-9]+}'
      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/mc.html'
          controller: 'mc.core.ui.states.controllers.DataModelTreeCtrl'

        'navbar-left@':
          template: '<contextual-menu></contextual-menu>'

        'navbar-right@':
          template: '<contextual-menu role="navigation-right" right="true"></contextual-menu>'
          controller: 'mc.core.ui.states.controllers.DataModelCtrl'

      resolve:
        currentDataModel: ['catalogue', '$rootScope', '$stateParams', '$q', 'catalogueElementResource', (catalogue, $rootScope, $stateParams, $q, catalogueElementResource) ->
          deferred = $q.defer()

          catalogueElementResource('dataModel').get($stateParams.dataModelId).then (dataModel) ->
            deferred.resolve(dataModel)

          deferred.promise
        ]

        lastSelectedElementHolder: -> {element: null}
    }

])