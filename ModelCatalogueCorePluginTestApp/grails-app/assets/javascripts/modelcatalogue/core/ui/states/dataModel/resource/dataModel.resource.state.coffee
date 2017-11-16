angular.module('mc.core.ui.states.dataModel.resource', [
  'mc.core.ui.states.dataModel.resource.diff',
  'mc.core.ui.states.dataModel.resource.list',
  'mc.core.ui.states.dataModel.resource.list-imported',
  'mc.core.ui.states.dataModel.resource.show',
  'mc.core.ui.states.dataModel.resource.xml-editor'

]).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc.resource', {
      abstract: true
      url: '/:resource'
      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/parent.html'
    }
])
