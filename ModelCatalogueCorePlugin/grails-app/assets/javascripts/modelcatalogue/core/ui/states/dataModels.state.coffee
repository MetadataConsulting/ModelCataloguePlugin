angular.module('mc.core.ui.states.dataModels', ['mc.core.ui.states.controllers.PanelsCtrl']).config([
  '$stateProvider',
  ($stateProvider) ->

    $stateProvider.state 'dataModels', {
      url: '/dataModels'

      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/panels.html'
          controller: 'mc.core.ui.states.controllers.PanelsCtrl'

      resolve:
        list: ['catalogueElementResource', (catalogueElementResource) ->
          catalogueElementResource('dataModel').list(status: 'active', minimal: true, max: 25)
        ]

      data:
        resource: 'dataModel'
        applicationTitle: 'Data Models'
        createDialogArgs:
          type: 'create-dataModel'
          create: 'dataModel'
    }

])